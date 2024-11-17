package com.yl3k.kbsf.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
@RequiredArgsConstructor
public class SocketIoServerLifeCycle {

    private final SocketIoEventHandler socketIoEventHandler;

    @PostConstruct
    public void start() {
        System.out.println("[WebSocket Server] - WebSocket server started.");
    }

    @PreDestroy
    public void stop() {
        System.out.println("[WebSocket Server] - WebSocket server stopped.");
    }
}
