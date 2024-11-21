package com.yl3k.kbsf.counsel.service;

import com.yl3k.kbsf.counsel.dto.CounselRoomDto;
import com.yl3k.kbsf.counsel.dto.CounselRoomRequestDto;
import com.yl3k.kbsf.counsel.entity.CounselRoom;
import com.yl3k.kbsf.counsel.entity.UserCounselRoom;
import com.yl3k.kbsf.counsel.repository.CounselRoomRepository;
import com.yl3k.kbsf.counsel.repository.UserCounselRoomRepository;
import com.yl3k.kbsf.user.entity.User;
import com.yl3k.kbsf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CounselRoomService {

    private final CounselRoomRepository counselRoomRepository;
    private final UserCounselRoomRepository userCounselRoomRepository;
    private final UserRepository userRepository;

    // 채팅방 생성
    @Transactional
    public Long createCounselRoom(CounselRoomRequestDto request) {
        try {
            CounselRoom newRoom = CounselRoom.builder()
                    .isHidden(false)
                    .build();

            CounselRoom savedRoom = counselRoomRepository.save(newRoom);
            Long roomId = savedRoom.getRoomId();
            long customerId = request.getCustomerId();
            long counselorId = request.getCounselorId();

            User user1 = userRepository.findById((int) customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid customerId"));
            User user2 = userRepository.findById((int) counselorId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid counselorId"));

            userCounselRoomRepository.save(UserCounselRoom.builder()
                    .user(user1)
                    .counselRoom(savedRoom)
                    .build());

            userCounselRoomRepository.save(UserCounselRoom.builder()
                    .user(user2)
                    .counselRoom(savedRoom)
                    .build());

            return roomId;
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public void updateStartedAt(Long roomId) {
        CounselRoom room = counselRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("CounselRoom with roomId " + roomId + " not found"));

        room.setStartedAt(LocalDateTime.now());
        counselRoomRepository.save(room);
    }

    @Transactional
    public void updateClosedAt(Long roomId) {
        CounselRoom room = counselRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("CounselRoom with roomId " + roomId + " not found"));

        room.setClosedAt(LocalDateTime.now());
        counselRoomRepository.save(room);
    }

    public Map<String, WebSocketSession> getClients(CounselRoomDto roomDto) {
        Optional<CounselRoomDto> counselRoomDto = Optional.ofNullable(roomDto);
        return counselRoomDto.get().getClients();
    }


    public void addClient(CounselRoomDto counselRoom, String userId, WebSocketSession session) {
        Map<String, WebSocketSession> clients = counselRoom.getClients();
        clients.put(userId, session);
    }

    public void removeClientById(CounselRoomDto counselRoom, String userID) {
        counselRoom.getClients().remove(userID);
    }
}
