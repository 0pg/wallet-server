package com.example.wallet.domain.exception;

public class Unexpected extends DomainException {
    public Unexpected(Throwable cause, String message) {
        super(cause, message);
    }
}
