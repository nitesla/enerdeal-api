package com.enerdeal.exceptions;

public class UserLoginException extends AbstractException {

    public UserLoginException(String code, String message) {
        super(code, message);
    }
}
