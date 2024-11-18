package com.yl3k.kbsf.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocketMessage {
    private String type;        // 메시지 타입 (offer, answer, candidate, end_call 등)
    private String customerId;
    private String counselorId;
    private Object sdp;
    private Object candidate;
}

