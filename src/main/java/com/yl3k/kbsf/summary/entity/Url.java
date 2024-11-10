package com.yl3k.kbsf.summary.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "url")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer urlId;

    @MapsId
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    private String name;

    private String url;
}
