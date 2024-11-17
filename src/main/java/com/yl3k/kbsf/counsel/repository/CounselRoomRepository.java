package com.yl3k.kbsf.counsel.repository;

import com.yl3k.kbsf.counsel.entity.CounselRoom;
import com.yl3k.kbsf.counsel.entity.UserCounselRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CounselRoomRepository extends JpaRepository<CounselRoom, Long> {

    @Query("SELECT cr FROM CounselRoom cr " +
            "WHERE cr.startedAt BETWEEN :startDate AND :endDate")
    List<CounselRoom> findByConsultationDateRange(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);

    /**
     * 특정 roomIds와 날짜 범위에 해당하는 roomIds 조회
     *
     * @param roomIds 조회할 room ID 목록
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @return 필터링된 room ID 목록
     */
    @Query("SELECT cr.roomId FROM CounselRoom cr WHERE cr.roomId IN :roomIds AND cr.closedAt BETWEEN :startDate AND :endDate")
    List<Long> findRoomIdsByDateRangeAndRoomIds(
            @Param("roomIds") List<Long> roomIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 특정 roomIds와 날짜 범위에 해당하는 상담 횟수 조회
     *
     * @param roomIds 조회할 room ID 목록
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @return 날짜 범위에 해당하는 총 상담 횟수
     */
    @Query("SELECT COUNT(cr) FROM CounselRoom cr WHERE cr.roomId IN :roomIds AND cr.closedAt BETWEEN :startDate AND :endDate")
    int countByRoomIdsAndDateRange(
            @Param("roomIds") List<Long> roomIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 특정 roomId를 기반으로 상담 종료 시간(closed_at) 조회
     *
     * @param roomId 상담 방 ID
     * @return 상담 종료 시간(Optional)
     */
    @Query("SELECT cr.closedAt FROM CounselRoom cr WHERE cr.roomId = :roomId")
    LocalDateTime findClosedAtByRoomId(@Param("roomId") Long roomId);

}