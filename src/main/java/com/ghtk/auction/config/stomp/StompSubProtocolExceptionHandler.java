package com.ghtk.auction.config.stomp;

import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.exception.StompExceptionHandler;
import com.ghtk.auction.service.StompService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompSubProtocolExceptionHandler extends StompSubProtocolErrorHandler {
  private final StompService stompService;
  private final StompExceptionHandler stompExceptionHandler;

  @Override
  public Message<byte[]> handleClientMessageProcessingError(
      Message<byte[]> clientMessage, Throwable ex) {
    try {  
      Optional<ApiResponse<?>> feedbackMessage
          = stompExceptionHandler.handle(ex, clientMessage);
      feedbackMessage.ifPresent(payload -> {
        System.out.println(payload);
        StompHeaderAccessor clientHeaders = StompHeaderAccessor.wrap(clientMessage);
        long userId = (Long) clientHeaders.getSessionAttributes().get("userId");
        stompService.sendMessageReceipt(userId, clientMessage, payload);
      });
      return null;
    } catch (Throwable e) {
      log.error("Error handling stomp message: " + e.getMessage());
      log.debug(null, e);
      return super.handleClientMessageProcessingError(clientMessage, e);
    }
  }
}
