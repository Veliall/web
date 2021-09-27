package org.example.app.exception;

public class IllegalCardAccessException extends RuntimeException{
    public IllegalCardAccessException() {
        super();
    }

    public IllegalCardAccessException(String message) {
        super(message);
    }

    public IllegalCardAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalCardAccessException(Throwable cause) {
        super(cause);
    }

    protected IllegalCardAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
