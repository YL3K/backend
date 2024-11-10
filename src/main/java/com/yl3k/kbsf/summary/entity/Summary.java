package com.yl3k.kbsf.summary.entity;

import com.yl3k.kbsf.counsel.entity.CounselRoom;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "summary")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long summaryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private CounselRoom counselRoom;

    @Column(length = 4000)
    private String summaryText;

    private String summaryShort;
}
