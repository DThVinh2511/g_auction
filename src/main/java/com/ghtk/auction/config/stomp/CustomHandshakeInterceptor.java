package com.ghtk.auction.config.stomp;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.ghtk.auction.config.CustomJwtDecoder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomHandshakeInterceptor implements HandshakeInterceptor {
  private final CustomJwtDecoder jwtDecoder;

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
      Map<String, Object> attributes) throws Exception {
    List<String> authorization = request.getHeaders().get("Authorization");
    if (authorization == null || authorization.isEmpty()) {
      return false;
    }
    if (authorization.size() > 1) {
      return false;
    }
    try {
      Jwt jwt = Optional.of(authorization.get(0))
        .filter(token -> token.startsWith("Bearer "))
        .map(token -> jwtDecoder.decode(token.substring(7)))
        .get();
      attributes.put("userId", jwt.getClaims().get("id"));
      attributes.put("userRole", ((String) jwt.getClaims().get("scope")).replace("ROLE_", ""));
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Exception exception) {
    // no-op
  }
}
