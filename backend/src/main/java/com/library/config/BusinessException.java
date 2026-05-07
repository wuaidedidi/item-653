package com.library.config;

import org.springframework.http.HttpStatus;

/**
 * 业务异常类
 */
public class BusinessException extends RuntimeException {
    private final Integer code;
    private final HttpStatus httpStatus;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(HttpStatus httpStatus, String message) {
        super(message);
        this.code = httpStatus.value();
        this.httpStatus = httpStatus;
    }

    public Integer getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
