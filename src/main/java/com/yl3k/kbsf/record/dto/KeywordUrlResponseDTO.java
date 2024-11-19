package com.yl3k.kbsf.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeywordUrlResponseDTO {
    private String keyword;
    private String urls;
}
