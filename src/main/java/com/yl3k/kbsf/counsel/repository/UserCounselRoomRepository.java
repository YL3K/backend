package com.yl3k.kbsf.counsel.repository;

import com.yl3k.kbsf.counsel.entity.UserCounselRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCounselRoomRepository extends JpaRepository<UserCounselRoom, Long> {

    /**
     * 특정 사용자 ID로 roomIds 조회
     *
     * @param userId 사용자 ID
     * @return room ID 목록
     */
    @Query("SELECT ucr.counselRoom.roomId FROM UserCounselRoom ucr WHERE ucr.user.userId = :userId")
    List<Long> findRoomIdsByUserId(@Param("userId") Integer userId);

    /**
     * 상담사 ID와 고객 이름으로 필터링된 roomIds 조회
     *
     * @param counselorId 상담사 ID
     * @param customerName 고객 이름
     * @return room ID 목록
     */

    @Query("SELECT ucr.counselRoom.roomId FROM UserCounselRoom ucr " +
            "WHERE ucr.counselRoom.roomId IN (SELECT ucr2.counselRoom.roomId FROM UserCounselRoom ucr2 WHERE ucr2.user.userId = :counselorId) " +
            "AND ucr.user.userName = :customerName")
    List<Long> findRoomIdsByCounselorIdAndCustomerName(@Param("counselorId") Integer counselorId, @Param("customerName") String customerName);

    /**
     * 특정 roomId를 기반으로 해당 방의 userId 목록 조회
     *
     * @param roomId 상담 방 ID
     * @return userId 목록 (고객 및 상담사)
     */
    @Query("SELECT ucr.user.userId FROM UserCounselRoom ucr WHERE ucr.counselRoom.roomId = :roomId")
    List<Integer> findUserIdsByRoomId(@Param("roomId") Long roomId);

    /**
     * 특정 roomId의 고객 userId 조회
     *
     * @param roomId 상담 방 ID
     * @return 고객의 userId (고객이 아닌 경우 null 반환 가능)
     */
    @Query("SELECT ucr.user.userId FROM UserCounselRoom ucr WHERE ucr.counselRoom.roomId = :roomId AND ucr.user.userType = 'customer'")
    Integer findCustomerIdByRoomId(@Param("roomId") Long roomId);

}
