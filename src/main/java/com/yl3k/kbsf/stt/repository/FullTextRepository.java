package com.yl3k.kbsf.stt.repository;

import com.yl3k.kbsf.stt.collection.FullText;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FullTextRepository extends MongoRepository<FullText, String> {
    FullText findByRoomId(Long roomId);
}
