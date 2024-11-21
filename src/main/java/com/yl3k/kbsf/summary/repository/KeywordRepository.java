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

    @Query(value = "SELECT k.keyword, k.description, u.url, COUNT(k.keyword_id) AS usage_count " +
            "FROM keyword k " +
            "JOIN summary_keyword sk ON k.keyword_id = sk.keyword_id " +
            "JOIN summary s ON sk.summary_id = s.summary_id " +
            "JOIN user_counsel_room ucr ON s.room_id = ucr.room_id " +
            "LEFT JOIN url u ON k.keyword_id = u.keyword_id " +
            "WHERE ucr.user_id = :userId " +
            "GROUP BY k.keyword, k.description, u.url " +
            "ORDER BY usage_count DESC, k.keyword ASC " +
            "LIMIT 5",
            nativeQuery = true)
    List<Object[]> findTop5KeywordsWithUrlsByUser(@Param("userId") Long userId);

    @Query(value = "SELECT k.keyword, u.url " +
            "FROM keyword k " +
            "JOIN summary_keyword sk ON k.keyword_id = sk.keyword_id " +
            "JOIN summary s ON sk.summary_id = s.summary_id " +
            "LEFT JOIN url u ON k.keyword_id = u.keyword_id " +
            "WHERE s.room_id = (" +
            "   SELECT ucr.room_id " +
            "   FROM user_counsel_room ucr " +
            "   JOIN counsel_room cr ON ucr.room_id = cr.room_id " +
            "   WHERE ucr.user_id = :userId " +
            "   ORDER BY cr.created_at DESC " +
            "   LIMIT 1" +
            ") " +
            "ORDER BY k.keyword ASC", nativeQuery = true)
    List<Object[]> findKeywordsWithUrlsByMostRecentRoom(@Param("userId") Long userId);

}