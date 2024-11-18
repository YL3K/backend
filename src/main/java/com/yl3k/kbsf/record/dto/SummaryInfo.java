package com.yl3k.kbsf.record.dto;

import com.yl3k.kbsf.counsel.entity.CounselRoom;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SummaryInfo {

    private Long summaryId;
    private CounselRoom counselRoom;
    private String summaryText;
    private String summaryShort;
    private String customerName;
}
