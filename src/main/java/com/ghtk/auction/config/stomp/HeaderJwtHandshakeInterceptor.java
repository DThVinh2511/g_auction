package com.ghtk.auction.config.stomp;

import java.util.List;
import java.util.Optional;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import com.ghtk.auction.config.CustomJwtDecoder;

@Component
public class HeaderJwtHandshakeInterceptor extends BasicJwtHandshakeInterceptor {
  public HeaderJwtHandshakeInterceptor(CustomJwtDecoder jwtDecoder) {
    super(jwtDecoder);
  }

  @Override
  public Optional<String> getToken(ServerHttpRequest request) {
    List<String> authorization = request.getHeaders().get("Authorization");
    if (authorization == null) {
      return Optional.empty();
    }
    return authorization.stream()
        .filter(token -> token.startsWith("Bearer "))
        .findFirst()
        .map(token -> token.substring("Bearer ".length()));
  }
}
