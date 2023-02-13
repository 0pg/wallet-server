package com.example.wallet.server.exception;

public class IOException extends ServerException {
    public IOException(String context, Throwable throwable) {
        super(context, throwable);
    }
}
