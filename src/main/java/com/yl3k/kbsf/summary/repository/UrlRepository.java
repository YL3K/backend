package com.yl3k.kbsf.summary.repository;

import com.yl3k.kbsf.summary.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query("SELECT u.url FROM Url u WHERE u.keyword.keywordId IN :keywordIds")
    List<String> findUrlsByKeywordIds(@Param("keywordIds") List<Integer> keywordIds);
}
