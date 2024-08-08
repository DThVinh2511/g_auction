package com.ghtk.auction.exception;

import com.ghtk.auction.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("ValidException: ", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(ApiResponse.error("ValidException", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Void>> paramsException(HandlerMethodValidationException ex) {
        log.error("HandlerMethodValidationException: ", ex);
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ApiResponse<Void>> emailException(EmailException ex) {
        log.error("EmailException: ", ex);
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> notFoundException(NotFoundException ex) {
        log.error("NotFoundException: ", ex);
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> accessException(AccessDeniedException ex) {
        log.error("NotFoundException: ", ex);
        return new ResponseEntity<>(ApiResponse.error("You do not have access"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticatedException.class)
    public ResponseEntity<ApiResponse<Void>> authenticateException(AuthenticatedException ex) {
        log.error("NotAuthenticateException: ", ex);
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> authenticateException(AlreadyExistsException ex) {
        log.error("AlreadyExistsException: ", ex);
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UploadException.class)
    public ResponseEntity<ApiResponse<Void>> authenticateException(UploadException ex) {
        log.error("UploadException: ", ex);
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> exception(Exception ex) {
        log.error("Exception", ex);
        return new ResponseEntity<>(ApiResponse.error("Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
