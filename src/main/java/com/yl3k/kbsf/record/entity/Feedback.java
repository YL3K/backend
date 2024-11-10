package com.yl3k.kbsf.record.entity;

import com.yl3k.kbsf.summary.entity.Summary;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summary_id")
    private Summary summary;

    @Column(length = 2000)
    private String feedback;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
}
