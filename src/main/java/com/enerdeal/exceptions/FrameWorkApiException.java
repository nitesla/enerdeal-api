package com.enerdeal.exceptions;


public class FrameWorkApiException extends RuntimeException{
    FrameWorkApiException(String message) {
        super(message);
    }

    FrameWorkApiException(String message, Throwable cause) {
        super(message, cause);
        if (this.getCause() == null && cause != null) {
            this.initCause(cause);
        }
    }
}
