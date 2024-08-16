package com.ghtk.auction.config.stomp;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import com.ghtk.auction.component.StompSessionManager;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SessionStoringWebDecorFactory implements WebSocketHandlerDecoratorFactory {
    private final StompSessionManager sessionManager;
    @Override
    public WebSocketHandler decorate(final WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
                sessionManager.addSession(session);
                super.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(final WebSocketSession session, final CloseStatus closeStatus) throws Exception {
                sessionManager.removeSession(session);
                super.afterConnectionClosed(session, closeStatus);
            }
        };
    }
  
}
