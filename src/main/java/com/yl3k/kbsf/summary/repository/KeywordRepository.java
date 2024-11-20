package com.yl3k.kbsf.summary.repository;

import com.yl3k.kbsf.summary.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Integer> {
  
    @Query("SELECT k.keyword FROM Keyword k")
    List<String> findAllKeywords();

    Optional<Keyword> findByKeyword(String keyword);
  
    /**
     *
     * @param keywordId 가장 높은 빈도의 keywordId
     * @return keyword  해당 keyword
     */

    @Query("SELECT k.keyword FROM Keyword k WHERE k.keywordId = :keywordId")
    String findKeywordById(@Param("keywordId") Integer keywordId);

    /**
     * 특정 keywordId 목록에 해당하는 Keyword 엔티티 조회
     *
     * @param keywordIds 조회할 keywordId 목록
     * @return keywordId 목록에 해당하는 Keyword 엔티티 리스트
     */
    @Query("SELECT k.keyword, u.url " +
            "FROM Keyword k " +
            "LEFT JOIN Url u ON k.keywordId = u.keyword.keywordId " +
            "WHERE k.keywordId IN :keywordIds")
    List<Object[]> findKeywordsAndUrls(@Param("keywordIds") List<Integer> keywordIds);

}