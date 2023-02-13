package com.example.wallet.server.exception;

public class DataNotFound extends ServerException {
    public DataNotFound(String context, String id) {
        super(context + " : " + id);
    }
}
