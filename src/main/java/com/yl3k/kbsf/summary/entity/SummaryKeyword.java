package com.yl3k.kbsf.summary.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "summary_keyword")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SummaryKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long summaryKeywordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summary_id")
    private Summary summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;
}
