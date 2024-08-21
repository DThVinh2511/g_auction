package com.ghtk.auction.config.stomp;

import java.util.Map;
import java.util.Optional;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.ghtk.auction.config.CustomJwtDecoder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BasicJwtHandshakeInterceptor implements HandshakeInterceptor {
  private final CustomJwtDecoder jwtDecoder;

  public abstract Optional<String> getToken(ServerHttpRequest request);

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
      Map<String, Object> attributes) throws Exception {
    try {
      Jwt jwt = getToken(request)
        .map(token -> jwtDecoder.decode(token))
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
