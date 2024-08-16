package com.ghtk.auction.config.stomp;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomHandshakeHandler implements HandshakeHandler {
  // private final JwtDecoder jwtDecoder;

  @Override
  public boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response, 
      WebSocketHandler wsHandler, Map<String, Object> attributes) 
          throws HandshakeFailureException {
    // attributes.put("testdohandshakeattributes", 12345);
    return true;
    // List<String> authorization = request.getHeaders().get("Authorization");
    // if (authorization == null || authorization.isEmpty()) {
    //   //   throw new HandshakeFailureExceptAuthorization header missingmissing");
    // }
    // if (authorization.size() > 1) {
    //   throw new HandshakeFailureException("Authorization header invalid");
    // }
    // try {
    //   Jwt jwt = Optional.of(authorization.get(0))
    //     .filter(token -> token.startsWith("Bearer "))
    //     .map(token -> jwtDecoder.decode(token.substring(7)))
    //     .get();
    //   attributes.put("jwt", jwt);
    //   return true;
    // } catch (Exception e) {
    //   throw new HandshakeFailureException("Authorization header invalid");
    // }
  }
}
