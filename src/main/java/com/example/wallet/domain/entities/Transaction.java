package com.example.wallet.domain.entities;

import java.math.BigInteger;

public record Transaction(
        String id,
        String srcAddress,
        String dstAddress,
        int confirmationCount,
        BigInteger amount,
        TransactionStatus status) {

    public Transaction(String id, String srcAddress, String dstAddress, BigInteger amount) {
        this(id, srcAddress, dstAddress, 0, amount, TransactionStatus.Pending);
    }

    public Transaction statusUpdated(TransactionStatus updatedStatus) {
        return new Transaction(id, srcAddress, dstAddress, confirmationCount, amount, updatedStatus);
    }

    public Transaction confirmed(int count) {
        return new Transaction(id, srcAddress, dstAddress, confirmationCount + count, amount, status);
    }
}
