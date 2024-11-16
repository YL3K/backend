package com.yl3k.kbsf.record.repository;

import com.yl3k.kbsf.record.entity.FullText;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FullTextRepository extends MongoRepository<FullText, Long> {
    List<FullText> findByRoomId(Long roomId);
}

