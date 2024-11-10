package com.yl3k.kbsf.summary.repository;

import com.yl3k.kbsf.summary.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
}
