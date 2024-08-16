package com.ghtk.auction.config.stomp;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public class DispatchAuthHandler implements AuthHandler {
  private final List<MatcherHandler> handlers;

  public DispatchAuthHandler(MatcherHandler... handlers) {
    this.handlers = new ArrayList<>(Arrays.asList(handlers));
  }

  @Override
  public void intercept(StompHeaderAccessor headers, Object payload) throws MessageHandlingException {
    for (MatcherHandler handler : handlers) { 
      Optional<Map<String, Object>> matchResult = handler.getMatcher().tryMatch(headers);
      if (matchResult.isPresent()) {
        addVarsToHeader(headers, matchResult.get());
        handler.getHandler().intercept(headers, payload);
        return;
      }
    }
  }

  // private static InterceptorMatcher getMatcher(AuthHandler handler) {
  //   AuthMapping mapping = handler.getClass().getAnnotation(AuthMapping.class);
  //   if (mapping == null) {
  //     throw new IllegalArgumentException("AuthHandler must be annotated with @AuthMapping");
  //   }
  //   return new InterceptorMatcher(
  //       Optional.of(mapping.path()).filter(path -> !path.isEmpty()), 
  //       Optional.of(mapping.command()).filter(com -> com != StompCommand.STOMP)
  //   );
  // }

  private static void addVarsToHeader(StompHeaderAccessor headers, Map<String, Object> vars) {
    vars.forEach(headers::setHeader);
  }
}
