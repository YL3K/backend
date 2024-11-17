package com.yl3k.kbsf.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoDTO {
    private Long summaryId; // 요약 ID
    private Integer userId; // 사용자 ID
    private String memo; // 메모 내용
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

