package com.yl3k.kbsf.record.dto;

import com.yl3k.kbsf.record.entity.Feedback;
import com.yl3k.kbsf.record.entity.FullText;
import com.yl3k.kbsf.summary.entity.Summary;
import com.yl3k.kbsf.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordDetailResponse {

    private Summary summary;
    private LocalDateTime counselDate;
    private String counselor;
    private User customer;
    private List<MemoResponseDTO> memos;
    private String feedback;
    private List<String> keywords;
    private String fullText;
}
