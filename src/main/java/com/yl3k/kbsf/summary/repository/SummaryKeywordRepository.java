package com.yl3k.kbsf.summary.repository;

import com.yl3k.kbsf.summary.entity.SummaryKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SummaryKeywordRepository extends JpaRepository<SummaryKeyword, Long> {
    @Query("SELECT k.keyword AS keyword, COUNT(sk) AS count FROM SummaryKeyword sk JOIN sk.keyword k GROUP BY k.keyword")
    List<Object[]> countKeywords();

    @Query("SELECT k.keyword, COUNT(sk) " +
            "FROM SummaryKeyword sk " +
            "JOIN sk.keyword k " +
            "JOIN sk.summary s " +
            "JOIN s.counselRoom cr " +
            "WHERE cr.startedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY cr.startedAt, k.keyword " +
            "ORDER BY cr.startedAt DESC")
    List<Object[]> countKeywordsByCounselRoomDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
