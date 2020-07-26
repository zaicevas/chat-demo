package com.tutorial.simplechat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private Set<WebSocketSession> activeSessions = new HashSet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("New WebSocket connection with id: ", session.getId());
        activeSessions = Stream.concat(activeSessions.stream(), Stream.of(session))
                .collect(Collectors.toSet());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        activeSessions = activeSessions.stream()
                .filter(s -> !session.equals(s))
                .collect(Collectors.toSet());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        activeSessions.forEach((ses) -> {
            try {
                ses.sendMessage(message);
            } catch (Exception ex) {
                log.error("Couldn't send WebSocket message to session with id: ", session.getId());
            }
        });
    }
}
