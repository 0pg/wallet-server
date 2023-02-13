package com.example.wallet.domain.entities;

public enum TransactionStatus {
    Pending, Mined, Confirmed, Failed;

    public boolean isOngoing() {
        return this == Pending || this == Mined;
    }
}
