package com.ghtk.auction.event;

import org.springframework.web.socket.WebSocketSession;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SessionClosedEvent {
  private final WebSocketSession session;
}
