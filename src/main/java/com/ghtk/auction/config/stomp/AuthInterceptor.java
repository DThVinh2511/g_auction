package com.ghtk.auction.config.stomp;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component("authInterceptor")
@RequiredArgsConstructor
public class AuthInterceptor implements ChannelInterceptor {
  private final DispatchAuthHandler authHandler;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
    Object payload = message.getPayload();
    authHandler.intercept(headers, payload);
    return MessageBuilder.createMessage(payload, headers.getMessageHeaders());
  }
}
