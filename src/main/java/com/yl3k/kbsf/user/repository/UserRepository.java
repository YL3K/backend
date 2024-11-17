package com.yl3k.kbsf.user.repository;

import com.yl3k.kbsf.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    // loginId와 일치하는 User 반환
    Optional<User> findByLoginId(String loginId);
    // loginId 중복 체크 : 존재하면 true, 존재하지 않으면 false 반환
    boolean existsByLoginId(String loginId);

    /**
     * 특정 userId 목록에 해당하며 user_type이 'counselor'인 사용자들을 조회
     *
     * @param userIds 조회할 userId 목록
     * @return user_type이 'counselor'인 User 엔티티 목록
     */
    @Query("SELECT u FROM User u WHERE u.userId IN :userIds AND u.userType = 'counselor'")
    List<User> findCounselorsByIds(@Param("userIds") List<Integer> userIds);


    /**
     * 특정 roomId에 해당하는 고객 정보 조회
     *
     * @param roomId 조회할 roomId
     * @return 고객 정보
     */
    @Query("SELECT u FROM User u JOIN UserCounselRoom ucr ON u.userId = ucr.user.userId WHERE ucr.counselRoom.roomId = :roomId AND u.userType = 'customer'")
    User findCustomerByRoomId(@Param("roomId") Long roomId);
  
}