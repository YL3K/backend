package com.yl3k.kbsf.record.service;

import com.yl3k.kbsf.counsel.entity.CounselRoom;
import com.yl3k.kbsf.counsel.repository.CounselRoomRepository;
import com.yl3k.kbsf.counsel.repository.UserCounselRoomRepository;
import com.yl3k.kbsf.record.dto.FeedbackDTO;
import com.yl3k.kbsf.record.dto.MemoDTO;
import com.yl3k.kbsf.record.dto.MemoResponseDTO;
import com.yl3k.kbsf.record.entity.Feedback;
import com.yl3k.kbsf.record.entity.Memo;
import com.yl3k.kbsf.record.repository.FeedbackRepository;
import com.yl3k.kbsf.stt.collection.FullText;
import com.yl3k.kbsf.stt.repository.FullTextRepository;
import com.yl3k.kbsf.record.repository.MemoRepository;
import com.yl3k.kbsf.summary.entity.Keyword;
import com.yl3k.kbsf.summary.entity.Summary;
import com.yl3k.kbsf.summary.repository.KeywordRepository;
import com.yl3k.kbsf.summary.repository.SummaryKeywordRepository;
import com.yl3k.kbsf.summary.repository.SummaryRepository;
import com.yl3k.kbsf.user.entity.User;
import com.yl3k.kbsf.user.entity.UserType;
import com.yl3k.kbsf.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCounselRoomRepository userCounselRoomRepository;

    @Autowired
    private CounselRoomRepository counselRoomRepository;

    @Autowired
    private SummaryRepository summaryRepository;

    @Autowired
    SummaryKeywordRepository summaryKeywordRepository;

    @Autowired
    KeywordRepository keywordRepository;

    @Autowired
    MemoRepository memoRepository;

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    FullTextRepository fullTextRepository;


    // User의 userType 확인 - 고객인 경우
    public boolean isCustomer(Integer userId) {
        // 1. userId를 통해 User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. userType이 UserType.CUSTOMER인지 확인
        return user.getUserType() == UserType.customer;
    }

    // User의 userType 확인 - 상담사인 경우
    public boolean isCounselor(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getUserType() == UserType.counselor;
    }


    /**
     * 고객의 경우: 상담 요약과 키워드 조회
     */
    public Map<String, Object> getFilteredSummariesForCustomer(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {

        // 1. userId에 해당하는 roomId 목록 조회
        List<Long> roomIds = userCounselRoomRepository.findRoomIdsByUserId(userId);

        // 2. 날짜 필터링된 roomIds 조회
        List<Long> filteredRoomIds = counselRoomRepository.findRoomIdsByDateRangeAndRoomIds(roomIds, startDate, endDate);

        // 3. 필터링 된 roomIds를 토대로 summaries 조회
        List<Summary> summaries = summaryRepository.findByRoomIds(filteredRoomIds);

        // 4. summaries에서 summaryIds 추출 후 키워드 랭킹 조회
        List<Long> summaryIds = summaries.stream()
                .map(Summary::getSummaryId)
                .collect(Collectors.toList());

        // 5. 가장 빈도가 높은 키워드 추출
        String topKeyword = getMostFrequentKeyword(summaryIds);

        // 결과 맵 생성
        Map<String, Object> result = new HashMap<>();
        result.put("count", summaries.size());
        result.put("topKeyword",topKeyword);
        result.put("summaries", summaries);


        return result;
    }

    /**
     * 상담사의 경우: 상담 건수 및 고객명 필터링 조회
     */
    public Map<String, Object> getFilteredSummariesForCounselor(Integer counselorId, LocalDateTime startDate, LocalDateTime endDate, String customerName) {
        List<Long> roomIds = userCounselRoomRepository.findRoomIdsByUserId(counselorId);
        int totalCount = counselRoomRepository.countByRoomIdsAndDateRange(roomIds, startDate, endDate);

        // 만약 추가 검색 있는 경우 (이름...)
        List<Long> filteredRoomIds = (customerName != null && !customerName.isEmpty())
                ? userCounselRoomRepository.findRoomIdsByCounselorIdAndCustomerName(counselorId, customerName)
                : roomIds;

        List<Summary> summaries = summaryRepository.findByRoomIdsAndDateRange(filteredRoomIds, startDate, endDate);
        // 고객 정보를 포함한 summaries 리스트 생성
        List<Map<String, Object>> summariesWithCustomerInfo = summaries.stream().map(summary -> {
            Map<String, Object> summaryMap = new HashMap<>();
            summaryMap.put("summaryId", summary.getSummaryId());
            summaryMap.put("summaryText", summary.getSummaryText());
            summaryMap.put("summaryShort", summary.getSummaryShort());
            summaryMap.put("counselRoom", summary.getCounselRoom());

            // roomId를 이용해 고객 userId를 조회하고, 고객 정보를 추가
            Long roomId = summary.getCounselRoom().getRoomId();
            Integer customerId = userCounselRoomRepository.findCustomerIdByRoomId(roomId); // 고객 userId 조회
            if (customerId != null) {
                User customer = userRepository.findById(customerId)
                        .orElseThrow(() -> new RuntimeException("Customer not found"));
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userId", customer.getUserId());
                userMap.put("userName", customer.getUsername());
                summaryMap.put("user", userMap); // 고객 정보를 user 필드에 추가
            }
            return summaryMap;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("filteredCounselCount", summaries.size());
        result.put("totalCounselCount", totalCount);
        result.put("summaries", summariesWithCustomerInfo);
        return result;
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

    /**
     * 요약 아이디 별 상세 내용
     */
    public Map<String, Object> getDetailedCounselInfo(Long summaryId) {
        Map<String, Object> result = new HashMap<>();

        // 1. Summary 정보 조회
        Summary summary = summaryRepository.findBySummaryId(summaryId)
                .orElseThrow(() -> new RuntimeException("Summary with ID " + summaryId + " not found."));

        CounselRoom counselRoom = summary.getCounselRoom();
        if (counselRoom == null) {
            throw new RuntimeException("CounselRoom is not linked to the given Summary.");
        }
        result.put("summary", summary);

        // 2. 상담 날짜 조회
        Long roomId = counselRoom.getRoomId();
        LocalDateTime closedAt = counselRoomRepository.findClosedAtByRoomId(roomId);
        result.put("counselDate", closedAt);

        // 3. 상담사 및 고객 정보 조회
        List<Integer> userIds = userCounselRoomRepository.findUserIdsByRoomId(roomId);
        if (userIds.isEmpty()) {
            throw new RuntimeException("No users linked to the given Room ID.");
        }

        // 상담사 조회
        List<User> counselors = userRepository.findCounselorsByIds(userIds);
        if (counselors.isEmpty()) {
            throw new RuntimeException("No counselors found for the given Room ID.");
        }
        result.put("counselor", counselors.get(0).getUsername());

        // 고객 조회
        User customer = userRepository.findCustomerByRoomId(roomId);
        if (customer == null) {
            throw new RuntimeException("Customer not found for the given Room ID.");
        }
        result.put("customer", customer);

        // 4. 메모 정보 조회
        List<Memo> memos = memoRepository.findBySummaryId(summaryId);
        List<MemoResponseDTO> memoDtos = memos.stream()
                .map(memo -> new MemoResponseDTO(memo.getMemoId(), memo.getMemo(), memo.getCreatedAt(), memo.getUpdatedAt()))
                .collect(Collectors.toList());
        result.put("memos", memoDtos.isEmpty() ? Collections.emptyList() : memoDtos);

        // 5. 상담사 피드백 정보 조회
        List<Feedback> feedback = feedbackRepository.findBySummaryId(summaryId);
        result.put("feedback", !feedback.isEmpty() ? feedback.get(0).getFeedback() : Collections.emptyList());

        // 6. 요약 키워드 조회
        List<Integer> keywordIds = summaryKeywordRepository.findKeywordIdsBySummaryId(summaryId);
        List<Keyword> keywords = keywordRepository.findKeywordsByIds(keywordIds);
        List<String> keywordList = keywords.stream()
                .map(Keyword::getKeyword)
                .collect(Collectors.toList());
        result.put("keywords", keywordList.isEmpty() ? Collections.emptyList() : keywordList);

        // 7. MongoDB에서 상담 내용 조회
        FullText fullText = fullTextRepository.findByRoomId(roomId);
        result.put("fullText", fullText == null ? Collections.emptyList() : fullText.getFullText());

        return result;
    }

    /**
     * 메모 삭제
     */

    public void deleteSummary(Long summaryId) {
        if (!summaryRepository.existsById(summaryId)) {
            throw new IllegalArgumentException("요약 ID가 유효하지 않습니다.");
        }
        summaryRepository.deleteById(summaryId);
    }




    /**
     * 상담사 피드백 저장
     */

    public void saveFeedback(FeedbackDTO feedbackDTO) {
        // Summary 조회
        Summary summary = summaryRepository.findById(feedbackDTO.getSummaryId())
                .orElseThrow(() -> new IllegalArgumentException("Summary ID가 유효하지 않습니다."));

        Feedback feedback = Feedback.builder()
                .summary(summary)
                .feedback(feedbackDTO.getFeedback())
                .build();

        feedbackRepository.save(feedback);
    }

    /**
     * 고객 메모 저장
     */

    public void saveMemo(MemoDTO memoDTO) {
        // Summary 조회
        Summary summary = summaryRepository.findById(memoDTO.getSummaryId())
                .orElseThrow(() -> new IllegalArgumentException("Summary ID가 유효하지 않습니다."));

        User user = userRepository.findById(memoDTO.getUserId())
                .orElseThrow(()-> new IllegalArgumentException("User ID가 유효하지 않습니다."));

        Memo insertMemo = Memo.builder()
                .summary(summary)
                .user(user)
                .memo(memoDTO.getMemo())
                .build();

        memoRepository.save(insertMemo);
    }

    /**
     * 메모 삭제
     */

    public void deleteMemo(Long memoId) {
        if (!memoRepository.existsById(memoId)) {
            throw new IllegalArgumentException("메모 ID가 유효하지 않습니다.");
        }
        memoRepository.deleteById(memoId);
    }

    /**
     * 메모 수정
     */
    public void updateMemo(Long memoId, Memo updatedMemo) {
        Memo existingMemo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("메모 ID가 유효하지 않습니다."));

        existingMemo.setMemo(updatedMemo.getMemo());
        existingMemo.setUpdatedAt(LocalDateTime.now());

        memoRepository.save(existingMemo);
    }


}
