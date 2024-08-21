package com.ghtk.auction.config.stomp;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;

import com.ghtk.auction.config.CustomJwtDecoder;

@Component
public class ProtocolJwtHandshakeInterceptor extends BasicJwtHandshakeInterceptor {
  public ProtocolJwtHandshakeInterceptor(CustomJwtDecoder jwtDecoder) {
    super(jwtDecoder);
  }

  @Override
  public Optional<String> getToken(ServerHttpRequest request) {
    List<String> protocol = request.getHeaders().get("Sec-WebSocket-Protocol");
    if (protocol == null || protocol.size() != 1) {
      return Optional.empty();
    }
    List<String> protocols = List.of(protocol.get(0).split(",\\s*"));
    for (int i = 0; i < protocols.size() - 1; i++) {
      System.out.println(protocols.get(i));
      if (protocols.get(i).equalsIgnoreCase("Authorization-Bearer")) {
        return Optional.of(protocols.get(i + 1));
      }
    }
    return Optional.empty();
  }

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
      Map<String, Object> attributes) throws Exception {
    if (super.beforeHandshake(request, response, wsHandler, attributes)) {
      response.getHeaders().add("Sec-WebSocket-Protocol", "Authorization-Bearer");
      return true;
    }
    return false;
  }
}
