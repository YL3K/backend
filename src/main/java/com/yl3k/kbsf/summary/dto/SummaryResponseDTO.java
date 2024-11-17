package com.yl3k.kbsf.summary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SummaryResponseDTO {
    private String summaryText;
    private String summaryShort;
}
