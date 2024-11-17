package com.yl3k.kbsf.record.controller;

import com.yl3k.kbsf.record.dto.FeedbackDTO;
import com.yl3k.kbsf.record.dto.MemoDTO;
import com.yl3k.kbsf.record.entity.Memo;
import com.yl3k.kbsf.record.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/record")
public class gRecordController {

    @Autowired
    private RecordService recordService;



    @GetMapping("/")
    public Map<String, Object> getFilteredSummariesByUserAndDate(
            @RequestParam Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String customerName
    ) {
        if (recordService.isCustomer(userId)) {
            return recordService.getFilteredSummariesForCustomer(userId, startDate, endDate);
        } else if (recordService.isCounselor(userId)) {
            return recordService.getFilteredSummariesForCounselor(userId, startDate, endDate, customerName);
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }
    }

    /**
     * 요약 삭제
     * @param summaryId
     */
    @DeleteMapping("/{summaryId}")
    public ResponseEntity<String> deleteSummary(@PathVariable Long summaryId) {
        recordService.deleteSummary(summaryId);
        return ResponseEntity.ok("요약이 성공적으로 삭제되었습니다.");
    }



    /**
     * 특정 summaryId를 기준으로 상담 요약 상세 정보 조회
     *
     * @param summaryId 조회할 summaryId
     * @return 상담 요약 상세 정보 및 고객, 상담사 정보
     */
    @GetMapping("/summary")
    public Map<String, Object> getSummaryDetails(@RequestParam Long summaryId) {
        return recordService.getDetailedCounselInfo(summaryId);
    }


    /**
     * 상담 상세 요약 별 상담사 피드백 저장
     * @param feedbackDTO
     */
    @PostMapping("/feedback")
    public ResponseEntity<String> saveFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        recordService.saveFeedback(feedbackDTO);
        return ResponseEntity.ok("피드백이 성공적으로 저장되었습니다.");
    }

    /**
     * 상담 상세 요약 별 고객 메모 저장
     * @param memoDTO
     */
    @PostMapping("/memo")
    public ResponseEntity<String> saveMemo(@RequestBody MemoDTO memoDTO) {
        recordService.saveMemo(memoDTO);
        return ResponseEntity.ok("메모가 성공적으로 저장되었습니다.");
    }

    /**
     * 메모 삭제
     * @param memoId
     */
    @DeleteMapping("/memo/{memoId}")
    public ResponseEntity<String> deleteMemo(@PathVariable Long memoId) {
        recordService.deleteMemo(memoId);
        return ResponseEntity.ok("메모가 성공적으로 삭제되었습니다.");
    }

    /**
     *  메모 수정
     * @param memoId, memo
     */
    @PatchMapping("/memo/{memoId}")
    public ResponseEntity<String> updateMemo(@PathVariable Long memoId, @RequestBody Memo memo) {
        recordService.updateMemo(memoId, memo);
        return ResponseEntity.ok("메모가 성공적으로 수정되었습니다.");
    }


}

