package com.tech.easy.common;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

@Getter
public class BaseResponse<T> {
    // Getters and setters
    private int code;
    private String message;
    private T data;
    private final String timestamp;
    private final String correlationId;

    // Formatter to match the log file's timestamp format and system default time zone.
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    // Default constructor that sets the timestamp and retrieves the requestId from MDC.
    public BaseResponse() {
        this.timestamp = TIMESTAMP_FORMATTER.format(Instant.now());
        this.correlationId = MDC.get("correlationId");
    }

    // Constructor with code, message, and data.
    public BaseResponse(int code, String message, T data) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<T> create(T data) {
        return new BaseResponse<>(201, "Created", data);
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, "Success", data);
    }

    public static BaseResponse<String> fail(int code, String message) {
        return new BaseResponse<>(code, message, StringUtils.EMPTY);
    }
}
