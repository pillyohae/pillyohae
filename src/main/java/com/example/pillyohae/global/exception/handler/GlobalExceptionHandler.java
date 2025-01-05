package com.example.pillyohae.global.exception.handler;

import com.example.pillyohae.global.exception.AuthenticationException;
import com.example.pillyohae.global.exception.BaseException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.global.response.CommonResponse;
import io.jsonwebtoken.JwtException;
import java.nio.file.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CommonResponse<String>> handle(BaseException e) {
        return CommonResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse<String>> handle(RuntimeException e) {
        log.error("RuntimeException : {}", e.getMessage());
        return CommonResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<String>> handle(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException : {}", e.getMessage());
        return CommonResponse.fail(ErrorCode.VALIDATION_ERROR, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CommonResponse<Object>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return CommonResponse.fail(ErrorCode.NOT_FOUND_USER, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CommonResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        return CommonResponse.fail(ErrorCode.INVALID_AUTHENTICATION, ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CommonResponse<String>> handleAuthenticationException(AuthenticationException ex) {
        return CommonResponse.fail(ErrorCode.INVALID_AUTHENTICATION, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        return CommonResponse.fail(ErrorCode.NOT_ALLOW_USER, ex.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<CommonResponse<String>> handleJwtException(JwtException ex) {
        return CommonResponse.fail(ErrorCode.BAD_REQUEST_TOKEN, ex.getMessage());
    }

}