package com.yl3k.kbsf.record.controller;

import com.yl3k.kbsf.global.response.dto.Message;
import com.yl3k.kbsf.global.response.response.ApiResponse;
import com.yl3k.kbsf.record.dto.*;
import com.yl3k.kbsf.record.entity.Memo;
import com.yl3k.kbsf.record.service.RecordService;
import com.yl3k.kbsf.user.entity.User;
import com.yl3k.kbsf.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/record")
@RequiredArgsConstructor
public class RecordController {

    private final AuthService authService;
    private final RecordService recordService;



    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<CustomerRecordResponse>> getFilteredSummariesByUserAndDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        User user = authService.getCurrentUser();
        Integer authUserId = user.getUserId();
        CustomerRecordResponse response = recordService.getFilteredSummariesForCustomer(authUserId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/counselor")
    public ResponseEntity<ApiResponse<CounselorRecordResponse>> getFilteredSummariesByUserAndDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String customerName
    ) {
        User user = authService.getCurrentUser();
        Integer authUserId = user.getUserId();
        CounselorRecordResponse response = recordService.getFilteredSummariesForCounselor(authUserId, startDate, endDate, customerName);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 요약 삭제
     * @param summaryId
     */
    @DeleteMapping("/{summaryId}")
    public ResponseEntity<ApiResponse<Message>> deleteSummary(@PathVariable Long summaryId) {

        recordService.deleteSummary(summaryId);
        Message message = Message.builder().message("Summary deleted successfully").build();
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * 특정 summaryId를 기준으로 상담 요약 상세 정보 조회
     *
     * @param summaryId 조회할 summaryId
     * @return 상담 요약 상세 정보 및 고객, 상담사 정보
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<RecordDetailResponse>> getSummaryDetails(@RequestParam Long summaryId) {

        RecordDetailResponse response = recordService.getDetailedCounselInfo(summaryId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 상담 상세 요약 별 상담사 피드백 저장
     * @param feedbackDTO
     */
    @PostMapping("/feedback")
    public ResponseEntity<ApiResponse<Message>> saveFeedback(@RequestBody FeedbackDTO feedbackDTO) {

        recordService.saveFeedback(feedbackDTO);
        Message message = Message.builder().message("Feedback created successfully.").build();
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * 상담 상세 요약 별 고객 메모 저장
     * @param memoDTO
     */
    @PostMapping("/memo")
    public ResponseEntity<ApiResponse<MemoResponseDTO>> saveMemo(@RequestBody MemoDTO memoDTO) {
        User user = authService.getCurrentUser();
        Integer authUserId = user.getUserId();
        memoDTO.setUserId(authUserId);
        MemoResponseDTO response = recordService.saveMemo(memoDTO);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 메모 삭제
     * @param memoId
     */
    @DeleteMapping("/memo/{memoId}")
    public ResponseEntity<ApiResponse<Message>> deleteMemo(@PathVariable Long memoId) {

        recordService.deleteMemo(memoId);
        Message message = Message.builder().message("Memo deleted successfully.").build();
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     *  메모 수정
     * @param memoId, memo
     */
    @PatchMapping("/memo/{memoId}")
    public ResponseEntity<ApiResponse<Message>> updateMemo(@PathVariable Long memoId, @RequestBody Memo memo) {

        recordService.updateMemo(memoId, memo);
        Message message = Message.builder().message("Memo updated successfully.").build();
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/counselorResponse")
    public ResponseEntity<ApiResponse<CounselorResponseDTO>> test(
            @RequestParam String choiceDate // "yyyy-MM" 형식으로 받음
    ){
        User user = authService.getCurrentUser();
        Integer authUserId = user.getUserId();
        CounselorResponseDTO result = recordService.getMonthlySummary(authUserId, choiceDate);
        return ResponseEntity.ok(ApiResponse.success(result));
    }


}
