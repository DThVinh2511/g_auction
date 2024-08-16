package com.ghtk.auction.event;

import org.springframework.messaging.Message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscribeEvent {
  private final Message<?> message;
}
