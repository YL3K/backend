package com.yl3k.kbsf.summary.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "keyword")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer keywordId;

    private String keyword;

    private String field;

    private String description;
}
