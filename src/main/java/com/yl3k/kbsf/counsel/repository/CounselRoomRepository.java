package com.yl3k.kbsf.counsel.repository;

import com.yl3k.kbsf.counsel.entity.CounselRoom;
import com.yl3k.kbsf.counsel.entity.UserCounselRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CounselRoomRepository extends JpaRepository<CounselRoom, Long> {
    @Query("SELECT cr FROM CounselRoom cr " +
            "WHERE cr.startedAt BETWEEN :startDate AND :endDate")
    List<CounselRoom> findByConsultationDateRange(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);
}
