package com.example.wallet.domain.exception;

import java.math.BigInteger;

public class InsufficientBalance extends DomainException {
    public InsufficientBalance(BigInteger balance, BigInteger tried) {
        super("current: " + balance + " < tried: " + tried);
    }
}
