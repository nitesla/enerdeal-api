package com.enerdeal.exceptions;

public class FailedRequestException extends AbstractException {

    public FailedRequestException(String code, String message) {
        super(code, message);

    }
}
