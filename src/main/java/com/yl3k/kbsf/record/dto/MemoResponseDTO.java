package com.yl3k.kbsf.record.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class MemoResponseDTO {
    private Long memoId;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
