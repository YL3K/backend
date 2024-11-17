package com.yl3k.kbsf.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDTO {
    private Long summaryId; // 요약 ID
    private String feedback; // 피드백 내용
}
