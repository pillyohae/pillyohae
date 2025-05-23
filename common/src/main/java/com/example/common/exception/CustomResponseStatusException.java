package com.example.common.exception;

import com.example.common.exception.code.ErrorCode;
import org.springframework.web.server.ResponseStatusException;

public class CustomResponseStatusException extends ResponseStatusException {

    private final ErrorCode errorCode;

    public CustomResponseStatusException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
