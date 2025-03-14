package com.tech.easy.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final int code;
    private final String message;
    @Getter
    private final Object[] args;

    public BaseException(int code, String message) {
        this.code = code;
        this.message = message;
        this.args = null;
    }

    public BaseException(int code, String message, Object[] args) {
        this.code = code;
        this.message = message;
        this.args = args;
    }

    public BaseException(DomainCode domainCode) {
        this.code = domainCode.getCode();
        this.message = domainCode.getMessage();
        this.args = null;
    }

    public BaseException(DomainCode domainCode, Object[] args) {
        this.code = domainCode.getCode();
        this.message = domainCode.getMessage();
        this.args = args;
    }

    public int getErrorCode() {
        return code;
    }

}