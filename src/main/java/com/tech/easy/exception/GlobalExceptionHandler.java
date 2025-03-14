package com.tech.easy.exception;

import com.tech.easy.common.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BizException.class)
    public ResponseEntity<BaseResponse<String>> handleBizException(BaseException ex) {
        log.error("Business exception thrown: {}", ex.getMessage(), ex);

        // Resolve the message from i18n properties file
        String localizedMessage = messageSource.getMessage(
                ex.getMessage(),
                ex.getArgs(),
                ex.getMessage(),
                LocaleContextHolder.getLocale());

        BaseResponse<String> response = BaseResponse.fail(ex.getCode(), localizedMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Catch any other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleAllOthers(Exception ex) {
        log.error("Unexpected exception occurred", ex);

        String localizedMessage = messageSource.getMessage(
                DomainCode.INTERNAL_SERVER_ERROR.getMessage(),
                null,
                "Internal Server Error",
                LocaleContextHolder.getLocale());

        BaseResponse<Object> response = new BaseResponse<>(
                DomainCode.INTERNAL_SERVER_ERROR.getCode(),
                localizedMessage,
                null
        );
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}