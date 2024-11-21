package com.yl3k.kbsf.counsel.repository;

import com.yl3k.kbsf.counsel.entity.CounselRoom;
import com.yl3k.kbsf.counsel.entity.UserCounselRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    @Query("SELECT cr.roomId FROM CounselRoom cr WHERE cr.roomId IN :roomIds AND " +
            "cr.isHidden = false AND cr.closedAt BETWEEN :startDate AND :endDate")
    List<Long> findRoomIdsByDateRangeAndCustomer(
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
    @Query("SELECT cr.roomId FROM CounselRoom cr WHERE cr.roomId IN :roomIds AND cr.closedAt BETWEEN :startDate AND :endDate")
    List<Long> findRoomIdsByDateRangeCounselor(
            @Param("roomIds") List<Long> roomIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 특정 roomId들을 기준으로 조회된 데이터 날짜순 정렬
     * @param roomIds
     * @return 필터링 된 counselRoom 데이터
     */
    @Query("SELECT cr FROM CounselRoom cr WHERE cr.roomId IN :roomIds ORDER BY cr.closedAt DESC")
    List<CounselRoom> findLatestByRoomIds(@Param("roomIds") List<Long> roomIds);

    /**
     * 특정 roomId를 기반으로 상담 종료 시간(closed_at) 조회
     *
     * @param roomId 상담 방 ID
     * @return 상담 종료 시간(Optional)
     */
    @Query("SELECT cr.closedAt FROM CounselRoom cr WHERE cr.roomId = :roomId")
    LocalDateTime findClosedAtByRoomId(@Param("roomId") Long roomId);


    @Modifying
    @Transactional
    @Query("UPDATE CounselRoom cr SET cr.isHidden = true WHERE cr.roomId = :roomId")
    int updateIsHiddenByRoomId(@Param("roomId") Long roomId);

}