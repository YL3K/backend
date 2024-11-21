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

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    // Setter 추가
    @Setter
    private LocalDateTime startedAt;

    @Setter
    private LocalDateTime closedAt;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isHidden;

}