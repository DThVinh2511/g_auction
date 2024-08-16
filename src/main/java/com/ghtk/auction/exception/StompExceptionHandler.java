package com.ghtk.auction.exception;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.stereotype.Component;

import com.ghtk.auction.component.StompSessionManager;
import com.ghtk.auction.dto.response.ApiResponse;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompExceptionHandler {
  private final ExceptionMatchHandler<?>[] exceptionHandlers;
  
  public Optional<ApiResponse<?>> handle(Throwable e, Message<?> message) {
    if (e instanceof MessageDeliveryException && e.getCause() != null) {
      e = e.getCause();
    }
    for (ExceptionMatchHandler<?> handler : exceptionHandlers) {
      @SuppressWarnings("deprecated")
      Optional<ApiResponse<?>> handled = handler.handle(e, message);
      if (handled != null) {
        System.out.println("Handled");
        return handled;
      }
    }
    log.error("Unhandled throwable:\n");
    e.printStackTrace();
    return Optional.empty();
  }

  @Bean
  public static ExceptionMatchHandler<?> unauthenticatedHandler(
      StompSessionManager sessionManager) {
    return new ExceptionMatchHandler<>(
        UnauthenticatedStompMessageException.class,
        (e, m) -> {
            System.out.println("Unauthenticated stomp message??");
            log.error("Unauthenticated stomp message:\n", e.getMessage());
            /// to trigger websocket close
            throw e;
            // try {
            //   WebSocketSession session = sessionManager.getSession(e.getSessionId());
            //   if (session == null) {
            //     return Optional.empty();
            //   }
            //   if (e.getCloseStatus() != null) {
            //     session.close(e.getCloseStatus());
            //   } else {
            //     session.close();
            //   }
            // } catch (Exception ex) {
            //   ex.printStackTrace();
            // }
            // return Optional.empty();
        });
  }

  @Bean
  public static ExceptionMatchHandler<?> unauthorizedHandler() {
    return new ExceptionMatchHandler<>(
        UnauthorizedStompMessageException.class,
        (e, m) -> {
            log.error("Unauthorized stomp message: " + e.getMessage());
            return Optional.of(ApiResponse.error(e.getMessage()));
        });
  }

  @Bean
  public static ExceptionMatchHandler<?> forbiddenHandler() {
    return new ExceptionMatchHandler<>(
        ForbiddenException.class,
        (e, m) -> {
            log.error("Forbidden : " + e.getMessage());
            return Optional.of(ApiResponse.error(e.getMessage()));
        });
  }

  @Bean
  public static ExceptionMatchHandler<?> validationExceptionHandler() {
    return new ExceptionMatchHandler<>(
        ValidationException.class,
        (e, m) -> {
            log.error("Forbidden : " + e.getMessage());
            return Optional.of(ApiResponse.error(e.getMessage()));
        });
  }
  
  @Bean
  public static ExceptionMatchHandler<?> notFoundExceptionHandler() {
    return new ExceptionMatchHandler<>(
        NotFoundException.class,
        (e, m) -> {
            log.error("Forbidden : " + e.getMessage());
            return Optional.of(ApiResponse.error(e.getMessage()));
        });
  }
  
  @Bean
  public static ExceptionMatchHandler<?> exceptionHandler() {
    return new ExceptionMatchHandler<>(
        Exception.class,
        (e, m) -> {
            log.error("Unhandled exception: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        });
  }

  public interface ExceptionHandler<T extends Throwable> {
    Optional<ApiResponse<?>> handle(T exception, Message<?> message);
  }

  @RequiredArgsConstructor
  public static class ExceptionMatchHandler<T extends Throwable> {
    private final Class<T> exceptionClass;
    private final ExceptionHandler<? super T> handler;

    /// This is deprecated because of weird api
    /// This function return a non-null Optional<Object> if the exception is handled
    /// and return null if the exception is not handled 
    @Deprecated
    public Optional<ApiResponse<?>> handle(Throwable e, Message<?> message) {
      if (exceptionClass.isInstance(e)) {
        return handler.handle(exceptionClass.cast(e), message);
      }
      return null;
    }
  }
}
