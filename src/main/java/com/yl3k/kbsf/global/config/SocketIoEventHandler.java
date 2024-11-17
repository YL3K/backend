package com.yl3k.kbsf.global.config;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * emit, listen 이벤트를 처리할 컨트롤러  (=이벤트핸들러, 컨트롤러 느낌)
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketIoEventHandler {

    private final SocketIOServer server;
    private final Map<String, SocketIOClient> clients = new ConcurrentHashMap<>();

    @PostConstruct
    public void setup() {
        server.addConnectListener(client -> log.info("[NettySocketio]-[onConnect]-[{}] Client connected", client.getSessionId()));
        server.addDisconnectListener(client -> {
            clients.values().remove(client);
            log.info("[NettySocketio]-[onDisconnect]-[{}] Client disconnected", client.getSessionId());
        });
        server.addEventListener("join_room", WebSocketMessage.class, onJoinRoom());
        server.addEventListener("offer", WebSocketMessage.class, onOffer());
        server.addEventListener("answer", WebSocketMessage.class, onAnswer());
        server.addEventListener("candidate", WebSocketMessage.class, onCandidate());
        server.addEventListener("end_call", WebSocketMessage.class, onEndCall());
    }

    private DataListener<WebSocketMessage> onJoinRoom() {
        return (client, data, askSender) -> {
            String roomId = generateRoomId(data.getCustomerId(), data.getCounselarId());
            log.info("[NettySocketio]-[onJoinRoom] Received message '{}'", data);
            server.getRoomOperations(roomId).sendEvent("join_room", data);
        };
    }

    private DataListener<WebSocketMessage> onOffer() {
        return (client, data, askSender) -> {
            String roomId = generateRoomId(data.getCustomerId(), data.getCounselarId());
            log.info("[NettySocketio]-[onOffer] Received offer for room '{}'", roomId);
            server.getRoomOperations(roomId).sendEvent("offer", data);
        };
    }

    private DataListener<WebSocketMessage> onAnswer() {
        return (client, data, askSender) -> {
            String roomId = generateRoomId(data.getCustomerId(), data.getCounselarId());
            log.info("[NettySocketio]-[onAnswer] Received answer for room '{}'", roomId);
            server.getRoomOperations(roomId).sendEvent("answer", data);
        };
    }

    private DataListener<WebSocketMessage> onCandidate() {
        return (client, data, askSender) -> {
            String roomId = generateRoomId(data.getCustomerId(), data.getCounselarId());
            log.info("[NettySocketio]-[onCandidate] Candidate received: {}", data);
            server.getRoomOperations(roomId).sendEvent("candidate", data);
        };
    }

    private DataListener<WebSocketMessage> onEndCall() {
        return (client, data, askSender) -> {
            String roomId = generateRoomId(data.getCustomerId(), data.getCounselarId());
            log.info("[NettySocketio]-[onEndCall] Ending call for room '{}'", roomId);
            server.getRoomOperations(roomId).sendEvent("end_call", data);
        };
    }

    private String generateRoomId(String userId1, String userId2) {
        return userId1 + "_" + userId2;
    }
}
