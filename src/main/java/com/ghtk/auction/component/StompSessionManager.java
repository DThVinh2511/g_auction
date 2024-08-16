package com.ghtk.auction.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class StompSessionManager {
  private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

  public void addSession(WebSocketSession session) {
    sessions.put(session.getId(), session);
  }

  public WebSocketSession getSession(String sessionId) {
    return sessions.get(sessionId);
  }

  public void removeSession(WebSocketSession session) {
    sessions.remove(session.getId());
  }
}
