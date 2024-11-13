package com.yl3k.kbsf.counsel.repository;

import com.yl3k.kbsf.counsel.entity.UserCounselRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserCounselRoomRepository extends JpaRepository<UserCounselRoom, Long> {
    @Query("SELECT ucr FROM UserCounselRoom ucr " +
            "JOIN ucr.counselRoom cr " +
            "WHERE cr.startedAt BETWEEN :startDate AND :endDate")
    List<UserCounselRoom> findByConsultationDateRange(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);
}
