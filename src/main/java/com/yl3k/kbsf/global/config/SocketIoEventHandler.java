package com.yl3k.kbsf.global.config;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class SocketIoEventHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();
    private static final Logger logger = Logger.getLogger(SocketIoEventHandler.class.getName());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String clientId = session.getId();
        clients.put(clientId, session);
        System.out.println("[WebSocket] - New connection: " + clientId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String clientId = session.getId();
        // 클라이언트로부터 받은 메시지 처리
        System.out.println("[WebSocket] - Received message from " + clientId + ": " + message.getPayload());

        // 예시로 클라이언트에게 받은 메시지를 그대로 전송
        session.sendMessage(new TextMessage(message.getPayload()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String clientId = session.getId();
        clients.remove(clientId);
        System.out.println("[WebSocket] - Connection closed: " + clientId);
    }

    // 커스텀 메소드로 메시지를 특정 클라이언트에게 보내는 방식
    public void sendMessageToClient(String clientId, String message) throws Exception {
        WebSocketSession session = clients.get(clientId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    // 클라이언트에게 'join_room' 이벤트 보내기
    public void sendJoinRoomMessage(String clientId, String message) throws Exception {
        sendMessageToClient(clientId, "join_room: " + message);
    }

    // 방별로 메시지 보내는 메소드
    public void sendMessageToRoom(String roomId, String message) throws Exception {
        for (WebSocketSession session : clients.values()) {
            // 방에 있는 세션을 확인하여 메시지 전송 (여기선 roomId 별로 방을 구분할 수 있어야 합니다)
            if (session.getAttributes().containsKey(roomId)) {
                sendMessageToClient(session.getId(), message);
            }
        }
    }

    // 방에 'join_room' 메시지를 전송
    public void handleJoinRoom(String roomId, String customerId, String counselorId) throws Exception {
        String message = "User " + customerId + " joined the room with " + counselorId;
        sendMessageToRoom(roomId, message);
    }

    // 오퍼 이벤트 처리
    public void handleOffer(String roomId, String offer) throws Exception {
        String message = "Offer received: " + offer;
        sendMessageToRoom(roomId, message);
    }

    // 답변 이벤트 처리
    public void handleAnswer(String roomId, String answer) throws Exception {
        String message = "Answer received: " + answer;
        sendMessageToRoom(roomId, message);
    }

    // 후보 이벤트 처리
    public void handleCandidate(String roomId, String candidate) throws Exception {
        String message = "Candidate received: " + candidate;
        sendMessageToRoom(roomId, message);
    }

    // 콜 종료 처리
    public void handleEndCall(String roomId) throws Exception {
        String message = "Call ended in room " + roomId;
        sendMessageToRoom(roomId, message);
    }
}
