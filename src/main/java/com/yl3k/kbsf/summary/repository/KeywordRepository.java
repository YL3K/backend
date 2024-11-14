package com.yl3k.kbsf.summary.repository;

import com.yl3k.kbsf.summary.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    @Query("SELECT k.keyword FROM Keyword k")
    List<String> findAllKeywords();

    Optional<Keyword> findByKeyword(String keyword);
}
