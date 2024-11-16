package com.yl3k.kbsf.summary.repository;

import com.yl3k.kbsf.summary.entity.SummaryKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SummaryKeywordRepository extends JpaRepository<SummaryKeyword, Long> {

    /**
     * 특정 Summary ID 목록에 대해 가장 많이 등장한 keywordId 조회
     *
     * @param summaryIds 조회할 Summary ID 목록
     * @return 주어진 Summary ID 목록에서 가장 빈도 높은 keywordId
     */
    @Query("SELECT sk.keyword.keywordId FROM SummaryKeyword sk WHERE sk.summary.summaryId IN :summaryIds " +
            "GROUP BY sk.keyword.keywordId " +
            "ORDER BY COUNT(sk.keyword.keywordId) DESC LIMIT 1")
    Integer findMostFrequentKeywordId(@Param("summaryIds") List<Long> summaryIds);


    /**
     * 특정 Summary ID에 연관된 keywordId 목록 조회
     *
     * @param summaryId 조회할 Summary ID
     * @return 해당 Summary ID에 연결된 keywordId 목록
     */
    @Query("SELECT sk.keyword.keywordId FROM SummaryKeyword sk WHERE sk.summary.summaryId = :summaryId")
    List<Integer> findKeywordIdsBySummaryId(@Param("summaryId") Long summaryId);
}
