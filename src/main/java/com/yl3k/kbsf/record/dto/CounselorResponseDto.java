package com.yl3k.kbsf.record.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CounselorResponseDto {
    private int totalCount;
    private String customerName;
    private LocalDateTime customerDate;

}
