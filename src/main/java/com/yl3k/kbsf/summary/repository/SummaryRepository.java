package com.yl3k.kbsf.summary.repository;

import com.yl3k.kbsf.summary.entity.Summary;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long> {

    /**
     * 특정 roomIds에 해당하는 Summary 목록 조회
     *
     * @param roomIds 조회할 room ID 목록
     * @return 필터링된 Summary 목록
     */
    @EntityGraph(attributePaths = {"counselRoom"})
    @Query("SELECT s FROM Summary s WHERE s.counselRoom.roomId IN :roomIds")
    List<Summary> findByRoomIds(@Param("roomIds") List<Long> roomIds);


    /**
     * 특정 roomIds와 날짜 범위에 해당하는 Summary 목록 조회
     *
     * @param roomIds 조회할 room ID 목록
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @return 필터링된 Summary 목록
     */
    @EntityGraph(attributePaths = {"counselRoom"})
    @Query("SELECT s FROM Summary s WHERE s.counselRoom.roomId IN :roomIds AND s.counselRoom.closedAt BETWEEN :startDate AND :endDate")
    List<Summary> findByRoomIdsAndDateRange(
            @Param("roomIds") List<Long> roomIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * summaryId를 기반으로 Summary 엔티티 조회
     *
     * @param summaryId 조회할 summary ID
     * @return 해당 summary ID에 대한 Summary 엔티티
     */
    @EntityGraph(attributePaths = {"counselRoom"})  // CounselRoom을 함께 로드
    @Query("SELECT s FROM Summary s WHERE s.summaryId = :summaryId")
    Optional<Summary> findBySummaryId(@Param("summaryId") Long summaryId);
}
