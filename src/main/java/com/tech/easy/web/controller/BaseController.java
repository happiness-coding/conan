package com.tech.easy.web.controller;

import com.tech.easy.common.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected <T> ResponseEntity<BaseResponse<T>> created201(String message, T data) {
        BaseResponse<T> response = new BaseResponse<>(201, message, data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    protected <T> ResponseEntity<BaseResponse<T>> ok200(String message, T data) {
        BaseResponse<T> response = new BaseResponse<>(200, message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    protected <T> ResponseEntity<BaseResponse<T>> noContent204(String message) {
        BaseResponse<T> response = new BaseResponse<>(204, message, null);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    protected <T> ResponseEntity<BaseResponse<T>> badRequest400(String message, T data) {
        BaseResponse<T> response = new BaseResponse<>(400, message, data);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
