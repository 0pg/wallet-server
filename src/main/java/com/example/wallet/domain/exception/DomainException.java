package com.example.wallet.domain.exception;

public abstract class DomainException extends RuntimeException {
    private Throwable cause;

    private String message;

    public DomainException(Throwable cause, String message) {
        super(message, cause);
    }

    public DomainException(String message) {
        super(message);
    }
}
