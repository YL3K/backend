package com.yl3k.kbsf.record.repository;

import com.yl3k.kbsf.record.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoRepository extends JpaRepository<Memo, Long> {
}
