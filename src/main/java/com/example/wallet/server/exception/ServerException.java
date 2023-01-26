package com.example.wallet.server.exception;

public abstract class ServerException extends RuntimeException {
    public ServerException(String context, Throwable throwable) {
        super(context, throwable);
    }

    public ServerException(String message) {
        super(message);
    }
}
