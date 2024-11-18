package com.yl3k.kbsf.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

public class SocketMessageUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper

    public static String serialize(SocketMessage socketMessage) throws IOException {
        return objectMapper.writeValueAsString(socketMessage);
    }

    public static SocketMessage deserialize(String payload) throws IOException {
        return objectMapper.readValue(payload, SocketMessage.class);
    }

    // SocketMessage -> WebSocket 메시지 변환
    public static TextMessage convertSocketMesageToWebSocketMessage(SocketMessage socketMessage) throws IOException {
        String jsonMessage = serialize(socketMessage);
        return new TextMessage(jsonMessage);
    }
}
