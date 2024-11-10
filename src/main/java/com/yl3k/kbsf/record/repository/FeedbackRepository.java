package com.yl3k.kbsf.record.repository;

import com.yl3k.kbsf.record.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
