package com.ghtk.auction.config.stomp;

import com.ghtk.auction.event.SubscribeEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConfirmSubscribeInterceptor implements ChannelInterceptor {
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
    StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
    if (StompCommand.SUBSCRIBE.equals(headers.getCommand())) {
      eventPublisher.publishEvent(new SubscribeEvent(message));
    }
  }
}
