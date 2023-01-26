package com.example.wallet.server.exception;

public class InvalidInput extends ServerException {
    public InvalidInput(String context, Throwable throwable) {
        super(context, throwable);
    }
}
