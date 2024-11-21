package com.yl3k.kbsf.websocket;

import com.yl3k.kbsf.counsel.dto.WaitingCustomerDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SocketEventHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SocketEventHandler.class);
    private final Map<String, WebSocketSession> users = new HashMap<>();
    private final Map<String, String> socketRoom = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("{} connected", session.getId());
        logger.info(String.valueOf(session));
        users.put(session.getId(), session);
        Map<String, String> curUser = new HashMap<>();
        curUser.put("userSessionId", session.getId());
        String jsonMessage = objectMapper.writeValueAsString(curUser);
        sendMessageToUser(session.getId(), jsonMessage);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        SocketMessage msgData = SocketMessageUtils.deserialize(payload);
        String type = msgData.getType();
        String roomId = getRoomId(msgData);

        switch (type) {
            case "join_room":
                handleJoinRoom(session, msgData, roomId);
                break;
            case "offer":
                handleOffer(session, msgData, roomId);
                break;
            case "answer":
                handleAnswer(session, msgData, roomId);
                break;
            case "candidate":
                handleCandidate(session, msgData, roomId);
                break;
            case "end_call":
                handleEndCall(session, roomId);
                break;
            default:
                logger.warn("Unknown type: {}", type);
        }
    }

    private static String getRoomId(SocketMessage msgData) {
        String customerId = msgData.getCustomerId();
        String counselorId = msgData.getCounselorId();
        return customerId + "_" + counselorId;
    }

    private void handleJoinRoom(WebSocketSession session, SocketMessage msgData, String roomId) throws IOException {
        if (socketRoom.containsValue(roomId)) {
            socketRoom.put(session.getId(), roomId);
            // 방에 있는 다른 사용자가 있다면 통신을 보내게됨
            msgData.setType("new_user");
            sendToRoomExcludeSelf(roomId, msgData, session.getId());
            logger.info("User {} joined room {}", session.getId(), roomId);
        } else {
            socketRoom.put(session.getId(), roomId);
            logger.info("No room for user {}. Created new room: {}", session.getId(), roomId);
        }
    }

    private void handleOffer(WebSocketSession session, SocketMessage msgData, String roomId) {
        sendToRoomExcludeSelf(roomId,  msgData, session.getId());
    }

    private void handleAnswer(WebSocketSession session, SocketMessage msgData, String roomId) {
        sendToRoomExcludeSelf(roomId,  msgData, session.getId());
    }

    private void handleCandidate(WebSocketSession session, SocketMessage msgData, String roomId) {
        sendToRoomExcludeSelf(roomId,  msgData, session.getId());
    }

    private void handleEndCall(WebSocketSession session, String roomId) {
        sendToRoom(roomId, new SocketMessage());
        users.values().forEach(user -> {
            if (socketRoom.get(user.getId()).equals(roomId)) {
                try {
                    user.sendMessage(new TextMessage("Call has ended"));
                } catch (IOException e) {
                    logger.error("Error sending message to {}: {}", user.getId(), e.getMessage());
                }
            }
        });
    }

    private void sendToRoom(String roomId, SocketMessage msgData) {
        users.values().forEach(user -> {
            String currentRoom = socketRoom.get(user.getId());
            if (currentRoom != null && currentRoom.equals(roomId)) {
                try {
                    if (user.isOpen()) {
                        TextMessage msg = SocketMessageUtils.convertSocketMesageToWebSocketMessage(msgData);
                        user.sendMessage(msg);
                        logger.info("Sent message to user {} in room {}: {}", user.getId(), roomId, msgData.getType());
                    }
                } catch (IOException e) {
                    logger.error("Error sending message to {}: {}", user.getId(), e.getMessage());
                }
            }
        });
    }

    private void sendToRoomExcludeSelf(String roomId, SocketMessage msgData, String myId) {
        socketRoom.forEach((sessionId, currentRoomId) -> {
            if (currentRoomId.equals(roomId) && !sessionId.equals(myId)) {
                WebSocketSession session = users.get(sessionId);
                if (session != null && session.isOpen()) {
                    try {
                        TextMessage msg = SocketMessageUtils.convertSocketMesageToWebSocketMessage(msgData);
                        session.sendMessage(msg);
                        logger.info("{} Sent message to user {} in room {}: {}", myId, sessionId, roomId, msgData.getType());
                    } catch (IOException e) {
                        logger.error("Error sending message to {}: {}", sessionId, e.getMessage());
                    }
                }
            }
        });
    }

    // 대기열 업데이트를 모든 클라이언트에 전송하는 메서드
    public void broadcastQueueUpdate(List<WaitingCustomerDto> waitingQueues) {
        try {
            Map<String, String> sendMsg = new HashMap<>();
            sendMsg.put("type", "queue_update");
            String queue = objectMapper.writeValueAsString(waitingQueues);
            sendMsg.put("queue", queue);

            String jsonMessage = objectMapper.writeValueAsString(sendMsg);

            users.values().forEach(user -> {
                try {
                    if (user.isOpen()) {
                        user.sendMessage(new TextMessage(jsonMessage));
                        logger.info("Broadcasting queue update: {}", jsonMessage);
                    }
                } catch (IOException e) {
                    logger.error("Error broadcasting message: {}", e.getMessage());
                }
            });
        } catch (IOException e) {
            logger.error("Error serializing queue update: {}", e.getMessage());
        }
    }

    // 특정 사용자에게 메시지 전송
    public void sendMessageToUser(String sessionId, String message) {
        WebSocketSession session = users.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                logger.info("Message sent to user {}: {}", sessionId, message);
            } catch (IOException e) {
                logger.error("Failed to send message to user {}: {}", sessionId, e.getMessage());
            }
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = socketRoom.get(session.getId());
        if (roomId != null) {
            socketRoom.remove(session.getId());
            users.remove(session.getId());
            logger.info("{} disconnected from room {}", session.getId(), roomId);
        }
    }
}
