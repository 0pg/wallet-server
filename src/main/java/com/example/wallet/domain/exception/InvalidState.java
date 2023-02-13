package com.example.wallet.domain.exception;

public class InvalidState extends DomainException {
    public InvalidState(String context, String message) {
        super(message);
    }
}
