package com.yl3k.kbsf.record.dto;

import com.yl3k.kbsf.summary.entity.Summary;
import com.yl3k.kbsf.user.entity.UserType;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerRecordResponse {

    private Integer count;
    private String topKeyword;
    private List<Summary> summaries;
    private UserType userType;
}
