package org.example.app.exception;

public class UnsupportedTransactionException extends RuntimeException {
    public UnsupportedTransactionException() {
        super();
    }

    public UnsupportedTransactionException(String message) {
        super(message);
    }

    public UnsupportedTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedTransactionException(Throwable cause) {
        super(cause);
    }

    protected UnsupportedTransactionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
