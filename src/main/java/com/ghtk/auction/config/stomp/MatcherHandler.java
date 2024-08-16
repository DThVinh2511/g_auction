package com.ghtk.auction.config.stomp;

import org.springframework.messaging.simp.stomp.StompCommand;

import lombok.Data;

@Data
public class MatcherHandler {
  InterceptorMatcher matcher;
  AuthHandler handler;

  public MatcherHandler(InterceptorMatcher matcher, AuthHandler handler) {
    this.matcher = matcher;
    this.handler = handler;
  }

  public MatcherHandler(AuthHandler handler) {
    this(new InterceptorMatcher(), handler);
  }

  public MatcherHandler(String path, AuthHandler handler) {
    this(new InterceptorMatcher(path), handler);
  }

  public MatcherHandler(StompCommand command, AuthHandler handler) {
    this(new InterceptorMatcher(command), handler);
  }

  public MatcherHandler(String path, StompCommand command, AuthHandler handler) {
    this(new InterceptorMatcher(path, command), handler);
  }
}