package org.example.app.exception;

public class IncorrectAmountException extends RuntimeException {
    public IncorrectAmountException() {
        super();
    }

    public IncorrectAmountException(String message) {
        super(message);
    }

    public IncorrectAmountException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectAmountException(Throwable cause) {
        super(cause);
    }

    protected IncorrectAmountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
