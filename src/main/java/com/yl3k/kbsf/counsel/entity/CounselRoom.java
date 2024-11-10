package com.yl3k.kbsf.counsel.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "counsel_room")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CounselRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isWaiting;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime closedAt;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isHidden;
}
