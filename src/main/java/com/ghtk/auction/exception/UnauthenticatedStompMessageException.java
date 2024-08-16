package com.ghtk.auction.exception;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.socket.CloseStatus;

import lombok.Getter;

@Getter
public class UnauthenticatedStompMessageException extends AuthenticationCredentialsNotFoundException {
  private String sessionId;
  private CloseStatus closeStatus;

  public UnauthenticatedStompMessageException(String sessionId, Optional<CloseStatus> closeStatus) {
    super("Unauthenticated stomp message");
    this.sessionId = sessionId;
    this.closeStatus = closeStatus.orElse(null);
  }

  public UnauthenticatedStompMessageException(String sessionId, Optional<CloseStatus> closeStatus, Throwable cause) {
    super("Unauthenticated stomp message", cause);
    this.sessionId = sessionId;
    this.closeStatus = closeStatus.orElse(null);
  }
}
