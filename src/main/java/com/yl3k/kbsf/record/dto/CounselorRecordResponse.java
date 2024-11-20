package com.yl3k.kbsf.record.dto;

import com.yl3k.kbsf.user.entity.UserType;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CounselorRecordResponse {

    private Integer filteredCounselCount;
    private Integer totalCounselCount;
    private List<SummaryInfo> summaries;
    private UserType userType;
}
