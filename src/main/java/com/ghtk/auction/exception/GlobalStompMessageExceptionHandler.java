package com.ghtk.auction.exception;

import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.service.StompService;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalStompMessageExceptionHandler {
  private final StompExceptionHandler stompExceptionHandler;
  private final StompService stompService;

  @MessageExceptionHandler(Exception.class)
  public void handleException(Exception e, Message<?> message, 
      @Header("userId") Long userId) {
    Optional<ApiResponse<?>> response = stompExceptionHandler.handle(e, message);
    response.ifPresent(payload -> {
      stompService.sendMessageReceipt(userId, message, payload);
    });
  }
}
