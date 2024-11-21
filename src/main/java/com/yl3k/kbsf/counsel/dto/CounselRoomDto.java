package com.yl3k.kbsf.counsel.dto;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
public class CounselRoomDto {
    @NotNull
    private final Long id;

    private final Map<String, WebSocketSession> clients = new HashMap<>();
}
