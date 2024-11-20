package com.yl3k.kbsf.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class SocketEventHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SocketEventHandler.class);

    // 사용자 - 세션 관리 맵과 사용자 - 방번호 관리 맵
    private final Map<String, WebSocketSession> users = new HashMap<>();
    private final Map<String, String> socketRoom = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("{} connected", session.getId());
        users.put(session.getId(), session);
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
