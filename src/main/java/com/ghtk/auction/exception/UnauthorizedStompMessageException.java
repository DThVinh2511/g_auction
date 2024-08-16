package com.ghtk.auction.exception;

import org.springframework.security.access.AccessDeniedException;
import lombok.Getter;

@Getter
public class UnauthorizedStompMessageException extends AccessDeniedException {
  public UnauthorizedStompMessageException(String message) {
    super(message);
  }

  public UnauthorizedStompMessageException(String message, Throwable cause) {
    super(message, cause);
  }
}
