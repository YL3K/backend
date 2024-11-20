package com.yl3k.kbsf.record.repository;

import com.yl3k.kbsf.record.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {

    /**
     *
     * @param summaryId
     * @return
     */
    @Query("SELECT m FROM Memo m JOIN FETCH m.summary LEFT JOIN FETCH m.user WHERE m.summary.summaryId = :summaryId ")
    List<Memo> findBySummaryId(@Param("summaryId") Long summaryId);

    @Query(value = "SELECT m.memo_id FROM memo m " +
            "WHERE m.summary_id = :summaryId " +
            "ORDER BY m.created_at DESC LIMIT 1", nativeQuery = true)
    Long findMemoId(@Param("summaryId") Long summaryId);

    @Query(value = "SELECT m.created_at FROM memo m " +
            "WHERE m.summary_id = :summaryId " +
            "ORDER BY m.created_at DESC LIMIT 1", nativeQuery = true)
    LocalDateTime findCreatedAt(@Param("summaryId") Long summaryId);
}
