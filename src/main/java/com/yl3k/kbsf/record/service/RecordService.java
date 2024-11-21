package com.yl3k.kbsf.record.service;

import com.yl3k.kbsf.counsel.entity.CounselRoom;
import com.yl3k.kbsf.counsel.repository.CounselRoomRepository;
import com.yl3k.kbsf.counsel.repository.UserCounselRoomRepository;
import com.yl3k.kbsf.global.response.error.ApplicationError;
import com.yl3k.kbsf.global.response.exception.ApplicationException;
import com.yl3k.kbsf.record.dto.*;
import com.yl3k.kbsf.record.entity.Feedback;
import com.yl3k.kbsf.record.entity.Memo;
import com.yl3k.kbsf.record.repository.FeedbackRepository;
import com.yl3k.kbsf.stt.collection.FullText;
import com.yl3k.kbsf.stt.repository.FullTextRepository;
import com.yl3k.kbsf.record.repository.MemoRepository;
import com.yl3k.kbsf.summary.entity.Keyword;
import com.yl3k.kbsf.summary.entity.Summary;
import com.yl3k.kbsf.summary.entity.Url;
import com.yl3k.kbsf.summary.repository.KeywordRepository;
import com.yl3k.kbsf.summary.repository.SummaryKeywordRepository;
import com.yl3k.kbsf.summary.repository.SummaryRepository;
import com.yl3k.kbsf.summary.repository.UrlRepository;
import com.yl3k.kbsf.user.entity.User;
import com.yl3k.kbsf.user.entity.UserType;
import com.yl3k.kbsf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final UserRepository userRepository;
    private final UserCounselRoomRepository userCounselRoomRepository;
    private final CounselRoomRepository counselRoomRepository;
    private final SummaryRepository summaryRepository;
    private final SummaryKeywordRepository summaryKeywordRepository;
    private final KeywordRepository keywordRepository;
    private final MemoRepository memoRepository;
    private final FeedbackRepository feedbackRepository;
    private final FullTextRepository fullTextRepository;
    private final UrlRepository urlRepository;

    public CustomerRecordResponse getFilteredSummariesForCustomer(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {

        List<Long> roomIds = userCounselRoomRepository.findRoomIdsByUserId(userId);
        List<Long> filteredRoomIds = counselRoomRepository.findRoomIdsByDateRangeAndCustomer(roomIds, startDate, endDate);
        List<Summary> summaries = summaryRepository.findByRoomIds(filteredRoomIds);
        List<Long> summaryIds = summaries.stream()
                .map(Summary::getSummaryId)
                .collect(Collectors.toList());
        String topKeyword = getMostFrequentKeyword(summaryIds);

        return CustomerRecordResponse.builder()
                .count(summaries.size())
                .topKeyword(topKeyword)
                .summaries(summaries)
                .userType(UserType.customer)
                .build();
    }

    public CounselorRecordResponse getFilteredSummariesForCounselor(Integer counselorId, LocalDateTime startDate, LocalDateTime endDate, String customerName) {

        List<Long> roomIds = userCounselRoomRepository.findRoomIdsByUserId(counselorId);

        Integer totalCount = counselRoomRepository.findRoomIdsByDateRangeCounselor(roomIds, startDate, endDate).size();

        List<Long> filteredRoomIds = (customerName != null && !customerName.isEmpty())
                ? userCounselRoomRepository.findRoomIdsByCounselorIdAndCustomerName(counselorId, customerName)
                : roomIds;

        List<SummaryInfo> summariesWithCustomerInfo = new ArrayList<>();
        List<Summary> summaries = summaryRepository.findByRoomIdsAndDateRange(filteredRoomIds, startDate, endDate);
        for (Summary summary : summaries) {

            Long roomId = summary.getCounselRoom().getRoomId();
            Integer customerId = userCounselRoomRepository.findCustomerIdByRoomId(roomId);
            User customer = userRepository.findById(customerId)
                    .orElseThrow(() -> new ApplicationException(ApplicationError.USER_NOT_FOUND));

            SummaryInfo info = SummaryInfo.builder()
                    .summaryId(summary.getSummaryId())
                    .counselRoom(summary.getCounselRoom())
                    .summaryText(summary.getSummaryText())
                    .summaryShort(summary.getSummaryShort())
                    .customerName(customer.getUsername())
                    .build();

            summariesWithCustomerInfo.add(info);
        }

        return CounselorRecordResponse.builder()
                .filteredCounselCount(summaries.size())
                .totalCounselCount(totalCount)
                .summaries(summariesWithCustomerInfo)
                .userType(UserType.counselor)
                .build();
    }

    /**
     * 가장 빈도 높은 키워드 출력
     */
    public String getMostFrequentKeyword(List<Long> summaryIds) {
        // 1. 가장 빈도 높은 keyword_id 찾기
        Integer mostFrequentKeywordId = summaryKeywordRepository.findMostFrequentKeywordId(summaryIds);

        if (mostFrequentKeywordId != null) {
            // 2. 해당 keyword_id로 keyword 테이블에서 키워드 값 조회
            return keywordRepository.findKeywordById(mostFrequentKeywordId);
        }
        return null; // 키워드가 없는 경우
    }

    public RecordDetailResponse getDetailedCounselInfo(Long summaryId) {

        Summary summary = summaryRepository.findBySummaryId(summaryId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.SUMMARY_NOT_FOUND));

        CounselRoom counselRoom = summary.getCounselRoom();
        if (counselRoom == null) {
            throw new ApplicationException(ApplicationError.COUNSEL_ROOM_NOT_FOUND);
        }

        Long roomId = counselRoom.getRoomId();
        LocalDateTime closedAt = counselRoomRepository.findClosedAtByRoomId(roomId);

        List<Integer> userIds = userCounselRoomRepository.findUserIdsByRoomId(roomId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.COUNSEL_MEMBER_NOT_FOUND));

        User counselors = userRepository.findCounselorsByIds(userIds)
                .orElseThrow(() -> new ApplicationException(ApplicationError.COUNSEL_MEMBER_NOT_FOUND));
        System.out.println("counselors : "+ counselors.getUsername());
        User customer = userRepository.findCustomerByRoomId(roomId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.COUNSEL_MEMBER_NOT_FOUND));
        System.out.println("customer : "+ customer.getUsername());

        List<Memo> memos = memoRepository.findBySummaryId(summaryId);
        List<MemoResponseDTO> memoDtos = memos.stream()
                .map(memo -> new MemoResponseDTO(memo.getMemoId(), memo.getMemo(), memo.getCreatedAt(), memo.getUpdatedAt()))
                .collect(Collectors.toList());

        List<Feedback> feedback = feedbackRepository.findBySummaryId(summaryId);

        List<Integer> keywordIds = summaryKeywordRepository.findKeywordIdsBySummaryId(summaryId);
        List<Object[]> keywordAndUrls = keywordRepository.findKeywordsAndUrls(keywordIds);


        List<KeywordUrlResponseDTO> keywordUrlList = keywordAndUrls.stream()
                .map(record -> new KeywordUrlResponseDTO((String) record[0], (String) record[1]))
                .collect(Collectors.toList());

        FullText fullText = fullTextRepository.findByRoomId(roomId);

        return RecordDetailResponse.builder()
                .summary(summary)
                .counselDate(closedAt)
                .counselor(counselors.getUsername())
                .customer(customer)
                .memos(!memoDtos.isEmpty() ? memoDtos : Collections.emptyList())
                .feedback(!feedback.isEmpty() ? feedback.get(0).getFeedback() : "")
                .keywords(!keywordUrlList.isEmpty() ? keywordUrlList : Collections.emptyList())
                .fullText(fullText != null ? fullText.getFullText() : "")
                .build();
    }

    /**
     * 메모 삭제
     */
    public void deleteSummary(Long summaryId) {
        if (!summaryRepository.existsById(summaryId)) {
            throw new ApplicationException(ApplicationError.SUMMARY_NOT_FOUND);
        }
        Optional<Summary> summary = summaryRepository.findBySummaryId(summaryId);
        Long roomId = summary.get().getCounselRoom().getRoomId();
        int updatedRows = counselRoomRepository.updateIsHiddenByRoomId(roomId);
        if (updatedRows == 0) {
            throw new ApplicationException(ApplicationError.COUNSEL_ROOM_NOT_FOUND);
        }
    }

    /**
     * 상담사 피드백 저장
     */
    public void saveFeedback(FeedbackDTO feedbackDTO) {
        // Summary 조회
        Summary summary = summaryRepository.findById(feedbackDTO.getSummaryId())
                .orElseThrow(() -> new ApplicationException(ApplicationError.SUMMARY_NOT_FOUND));

        Feedback feedback = Feedback.builder()
                .summary(summary)
                .feedback(feedbackDTO.getFeedback())
                .build();

        feedbackRepository.save(feedback);
    }

    /**
     * 고객 메모 저장
     */
    public MemoResponseDTO saveMemo(MemoDTO memoDTO) {
        // Summary 조회
        Summary summary = summaryRepository.findById(memoDTO.getSummaryId())
                .orElseThrow(() -> new ApplicationException(ApplicationError.SUMMARY_NOT_FOUND));

        User user = userRepository.findById(memoDTO.getUserId())
                .orElseThrow(()-> new ApplicationException(ApplicationError.USER_NOT_FOUND));

        Memo insertMemo = Memo.builder()
                .summary(summary)
                .user(user)
                .memo(memoDTO.getMemo())
                .build();
        memoRepository.save(insertMemo);
        Long nowMemoId = memoRepository.findMemoId(memoDTO.getSummaryId());
        LocalDateTime nowMemoTime = memoRepository.findCreatedAt(memoDTO.getSummaryId());
        MemoResponseDTO nowMemo = MemoResponseDTO.builder()
                .memoId(nowMemoId)
                .createdAt(nowMemoTime)
                .build();


        return nowMemo;
    }

    /**
     * 메모 삭제
     */
    public void deleteMemo(Long memoId) {
        if (!memoRepository.existsById(memoId)) {
            throw new ApplicationException(ApplicationError.MEMO_NOT_FOUND);
        }
        memoRepository.deleteById(memoId);
    }

    /**
     * 메모 수정
     */
    public void updateMemo(Long memoId, Memo updatedMemo) {
        Memo existingMemo = memoRepository.findById(memoId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.MEMO_NOT_FOUND));

        existingMemo.setMemo(updatedMemo.getMemo());
        existingMemo.setUpdatedAt(LocalDateTime.now());

        memoRepository.save(existingMemo);
    }


    public CustomerCurrentDTO getRecentCustomer(Integer userId){

        // 1. 입력받은 userId 기준 roomId들 조회
        List<Long> roomIds = userCounselRoomRepository.findRoomIdsByUserId(userId);
        System.out.println("roomIds : " + roomIds);

        if (roomIds.isEmpty()) {
            return CustomerCurrentDTO.builder()
                    .customerName(null)
                    .customerDate(null)
                    .build();
        }


        // 2. roomId를 기준으로 제일 최근 데이터 하나 불러오기
        List<CounselRoom> filteredCounselRooms = counselRoomRepository.findLatestByRoomIds(roomIds);

        if (filteredCounselRooms.isEmpty()) {
            return CustomerCurrentDTO.builder()
                    .customerName(null)
                    .customerDate(null)
                    .build();
        }

        Long filteredRoomId = filteredCounselRooms.get(0).getRoomId();


        // 조회된 RoomId로 유저 정보 구하기 + 해당 방의 종료시간 구하기
        Optional<User> filterUser = userRepository.findCustomerByRoomId(filteredRoomId);
        String recentUsername = filterUser.get().getUsername();
        LocalDateTime filterdRoomTime = filteredCounselRooms.get(0).getClosedAt();

        // 구한 값들 Dto에 적용
        CustomerCurrentDTO responseCounselorDto = CustomerCurrentDTO.builder()
                .customerName(recentUsername)
                .customerDate(filterdRoomTime)
                .build();

        return responseCounselorDto;


    }



    public Integer getMonthlySummaryCount(Integer userId, String choiceDate){
        // 0. choiceDate를 기준으로 startDate와 endDate 구하기
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(choiceDate, formatter);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay(); // 월 첫째 날 00:00:00
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59); // 월 마지막 날 23:59:59


        // 1. 입력받은 userId 기준 roomId들 조회
        List<Long> roomIds = userCounselRoomRepository.findRoomIdsByUserId(userId);
        System.out.println("roomIds : " + roomIds);

        if (roomIds.isEmpty()) {
            return 0;
        }


        // 2. roomId와 날짜를 기준으로 roomId들 거르기 + 개수 구하기
        List<Long> filteredRoomIds = counselRoomRepository.findRoomIdsByDateRangeCounselor(roomIds, startDate, endDate);
        int totalCount = filteredRoomIds.size();

        if (filteredRoomIds.isEmpty()) {
            return 0;
        }


        return totalCount;


    }

}
