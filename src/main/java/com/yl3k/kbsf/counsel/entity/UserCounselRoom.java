package com.yl3k.kbsf.counsel.entity;

import com.yl3k.kbsf.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_counsel_room")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCounselRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private CounselRoom counselRoom;
}
