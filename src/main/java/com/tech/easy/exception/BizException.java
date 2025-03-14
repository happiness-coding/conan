package com.tech.easy.exception;

import lombok.Getter;

/**
 * Business exception class for handling domain-specific errors.
 * Use this class for exceptions that represent business rule violations
 * or application-specific error conditions.
 */
@Getter
public class BizException extends BaseException {

    /**
     * Creates a business exception with the specified domain code.
     *
     * @param domainCode The domain code representing the business exception
     */
    public BizException(DomainCode domainCode) {
        super(domainCode);
    }

    /**
     * Creates a business exception with the specified domain code and message arguments.
     *
     * @param domainCode The domain code representing the business exception
     * @param args Arguments to be used in the message template
     */
    public BizException(DomainCode domainCode, Object[] args) {
        super(domainCode, args);
    }

    /**
     * Creates a business exception with the specified error code and message.
     *
     * @param code The error code
     * @param message The error message (typically an i18n key)
     */
    public BizException(int code, String message) {
        super(code, message);
    }

    /**
     * Creates a business exception with the specified error code, message, and message arguments.
     *
     * @param code The error code
     * @param message The error message (typically an i18n key)
     * @param args Arguments to be used in the message template
     */
    public BizException(int code, String message, Object[] args) {
        super(code, message, args);
    }
}