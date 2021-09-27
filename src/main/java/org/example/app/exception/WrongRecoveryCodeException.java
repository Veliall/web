package org.example.app.exception;

public class WrongRecoveryCodeException extends RuntimeException {
    public WrongRecoveryCodeException() {
        super();
    }

    public WrongRecoveryCodeException(String message) {
        super(message);
    }

    public WrongRecoveryCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongRecoveryCodeException(Throwable cause) {
        super(cause);
    }

    protected WrongRecoveryCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
