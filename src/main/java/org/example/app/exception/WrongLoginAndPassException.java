package org.example.app.exception;

public class WrongLoginAndPassException extends RuntimeException {
    public WrongLoginAndPassException() {
        super();
    }

    public WrongLoginAndPassException(String message) {
        super(message);
    }

    public WrongLoginAndPassException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongLoginAndPassException(Throwable cause) {
        super(cause);
    }

    protected WrongLoginAndPassException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
