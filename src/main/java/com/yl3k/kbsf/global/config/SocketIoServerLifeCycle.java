package com.yl3k.kbsf.global.config;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * SocketIOSever 의 생명주기 관리
 * SocketIOServer의 빈을 의존성 주입받아 서버를 시작하고 컨텍스트 내려갈 때 종료
 * 그냥 맨처음에 만들어진다고 생각하면 됨
 */

@Component
@RequiredArgsConstructor
public class SocketIoServerLifeCycle {
    private final SocketIOServer server;

    @PostConstruct
    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
