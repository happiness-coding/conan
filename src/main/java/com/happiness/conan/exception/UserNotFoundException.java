package com.happiness.conan.exception;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(Long userId) {
        super(404, "User not found, id=" + userId);
    }
}
