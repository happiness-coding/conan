package com.tech.easy.exception;

import lombok.Getter;

@Getter
public enum DomainCode {

    // Technical errors (5xx)
    INTERNAL_SERVER_ERROR(500, "error.technical.internal"),
    SERVICE_UNAVAILABLE(503, "error.technical.service.unavailable"),
    DATABASE_ERROR(500, "error.technical.database"),
    VALIDATION_ERROR(400, "error.technical.validation"),
    UNAUTHORIZED(401, "error.technical.unauthorized"),
    FORBIDDEN(403, "error.technical.forbidden"),

    // Business errors (4xx)
    BAD_REQUEST(400, "error.business.bad.request"),
    USER_NOT_FOUND(404, "error.business.user.not.found"),
    RESOURCE_NOT_FOUND(404, "error.business.resource.not.found"),
    RESOURCE_ALREADY_EXISTS(409, "error.business.resource.exists"),
    INVALID_INPUT(400, "error.business.invalid.input"),
    USER_ALREADY_EXISTS(409, "error.business.user.exists"),
    INSUFFICIENT_PERMISSIONS(403, "error.business.insufficient.permissions"),
    INVALID_OPERATION(400, "error.business.invalid.operation"),
    BUSINESS_RULE_VIOLATION(422, "error.business.rule.violation");

    private final int code;
    private final String message;

    DomainCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}