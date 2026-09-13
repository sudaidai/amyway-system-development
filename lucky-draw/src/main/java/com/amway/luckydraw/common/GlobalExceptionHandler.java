package com.amway.luckydraw.common;

import java.time.Instant;
import org.springframework.http.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  ResponseEntity<ApiError> optimisticLock(ObjectOptimisticLockingFailureException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            new ApiError(
                ErrorCode.PRIZE_CONCURRENT_MODIFICATION,
                "Prize was modified by another transaction; reload and retry",
                Instant.now()));
  }

  @ExceptionHandler(BusinessException.class)
  ResponseEntity<ApiError> business(BusinessException e) {
    HttpStatus status =
        switch (e.getCode()) {
          case CAMPAIGN_NOT_FOUND, PRIZE_NOT_FOUND -> HttpStatus.NOT_FOUND;
          case CAMPAIGN_NOT_AVAILABLE, DRAW_LIMIT_EXCEEDED -> HttpStatus.CONFLICT;
          case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
          case FORBIDDEN -> HttpStatus.FORBIDDEN;
          default -> HttpStatus.BAD_REQUEST;
        };
    return ResponseEntity.status(status)
        .body(new ApiError(e.getCode(), e.getMessage(), Instant.now()));
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
  ResponseEntity<ApiError> invalid(Exception e) {
    String message =
        e instanceof MethodArgumentNotValidException m
            ? m.getBindingResult().getAllErrors().getFirst().getDefaultMessage()
            : e.getMessage();
    return ResponseEntity.badRequest()
        .body(new ApiError(ErrorCode.INVALID_DRAW_COUNT, message, Instant.now()));
  }
}
