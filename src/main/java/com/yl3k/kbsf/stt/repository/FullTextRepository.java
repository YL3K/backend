package com.yl3k.kbsf.stt.repository;

import com.yl3k.kbsf.stt.collection.FullText;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FullTextRepository extends MongoRepository<FullText, String> {
}
