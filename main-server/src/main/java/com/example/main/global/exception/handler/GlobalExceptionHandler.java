package com.example.main.global.exception.handler;

import com.example.common.exception.BaseException;
import com.example.common.exception.CustomResponseStatusException;
import com.example.common.exception.code.ErrorCode;
import com.example.main.global.response.CommonResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
        return CommonResponse.fail(ErrorCode.VALIDATION_ERROR,
            Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CommonResponse<Object>> handleUsernameNotFoundException(
        UsernameNotFoundException ex) {
        return CommonResponse.fail(ErrorCode.NOT_FOUND_USER, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CommonResponse<Object>> handleBadCredentialsException(
        BadCredentialsException ex) {
        return CommonResponse.fail(ErrorCode.INVALID_PASSWORD, ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CommonResponse<String>> handleAuthenticationException(
        AuthenticationException ex) {
        return CommonResponse.fail(ErrorCode.UNAUTHORIZED_TOKEN, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<String>> handleAccessDeniedException(
        AccessDeniedException ex) {
        return CommonResponse.fail(ErrorCode.NOT_ALLOW_USER, ex.getMessage());
    }

    @ExceptionHandler(CustomResponseStatusException.class)
    public ResponseEntity<CommonResponse<String>> handleCustomResponseStatusException(
        CustomResponseStatusException ex) {
        return CommonResponse.fail(ex.getErrorCode(), ex.getMessage());
    }

}
