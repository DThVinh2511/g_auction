package com.ghtk.auction.config.stomp;

import java.util.Objects;
import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;

import com.ghtk.auction.config.CustomJwtDecoder;
import com.ghtk.auction.exception.UnauthenticatedStompMessageException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("jwtInterceptor")
@RequiredArgsConstructor
public class JwtInterceptor implements ChannelInterceptor {
  private final CustomJwtDecoder jwtDecoder;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
    Object payload = message.getPayload();
    var command = headers.getCommand();
    if (command != StompCommand.CONNECT 
      && command != StompCommand.SUBSCRIBE 
      && command != StompCommand.SEND
      && command != StompCommand.MESSAGE
      && command != StompCommand.BEGIN
      && command != StompCommand.COMMIT) {
      return message;
    }
    checkJwt(headers);
    headers.setHeader("userId", headers.getSessionAttributes().get("userId"));
    headers.setHeader("userRole", headers.getSessionAttributes().get("userRole"));
    return MessageBuilder.createMessage(payload, headers.getMessageHeaders());
  }

  private void checkJwt(StompHeaderAccessor headers) {
    String token = headers.getFirstNativeHeader("Authorization");
    System.out.println(headers);
    if (token == null) {
      throw new UnauthenticatedStompMessageException(headers.getSessionId(), Optional.of(CloseStatus.NOT_ACCEPTABLE));
    }
    try {
      if (!token.startsWith("Bearer ")) {
        throw new UnauthenticatedStompMessageException(headers.getSessionId(), Optional.of(CloseStatus.NOT_ACCEPTABLE));
      }
      Jwt jwt = jwtDecoder.decode(token.substring(7));
      if (!Objects.equals(jwt.getClaims().get("id"), headers.getSessionAttributes().get("userId"))) {
        throw new Exception("User id in token does not match user id in session");
      }
      System.out.println("OK");
    } catch (Exception e) {
      throw new UnauthenticatedStompMessageException(headers.getSessionId(), Optional.of(CloseStatus.NOT_ACCEPTABLE), e);
    }
  }
  
}
