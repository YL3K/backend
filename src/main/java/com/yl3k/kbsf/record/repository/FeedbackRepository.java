package com.yl3k.kbsf.record.repository;

import com.yl3k.kbsf.record.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    /**
     *
     * @param summaryId
     * @return
     */
    @Query("SELECT f FROM Feedback f WHERE f.summary.summaryId = :summaryId")
    List<Feedback> findBySummaryId(@Param("summaryId") Long summaryId);
}
