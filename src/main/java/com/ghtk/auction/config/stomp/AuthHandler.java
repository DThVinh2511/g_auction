package com.ghtk.auction.config.stomp;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface AuthHandler {
  void intercept(StompHeaderAccessor accessor, Object payload);
}
