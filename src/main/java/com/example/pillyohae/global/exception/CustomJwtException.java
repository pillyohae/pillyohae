package com.example.pillyohae.global.exception;

import com.example.pillyohae.global.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class CustomJwtException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomJwtException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomJwtException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}