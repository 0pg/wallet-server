package com.example.wallet.domain.entities.eth;

import java.math.BigDecimal;

public class Transaction {
    private static final int BLOCK_COUNT_FOR_CONFIRMATION = 12;

    private final String id;
    private int confirmationCount;

    public final BigDecimal withdrawAmount;
    public TransactionStatus status;

    public Transaction(String id, BigDecimal balanceChanges) {
        this.id = id;
        this.withdrawAmount = balanceChanges;
        this.status = TransactionStatus.Pending;
        this.confirmationCount = 0;
    }

    public String id() {
        return id;
    }

    public void commit() {
        confirmationCount += Math.min(confirmationCount + 1, BLOCK_COUNT_FOR_CONFIRMATION);
        switch (status) {
            case Pending -> {
                if (confirmationCount < BLOCK_COUNT_FOR_CONFIRMATION) {
                    status = TransactionStatus.Mined;
                } else {
                    status = TransactionStatus.Confirmed;
                }
            }
            case Mined -> {
                if (confirmationCount >= BLOCK_COUNT_FOR_CONFIRMATION) {
                    status = TransactionStatus.Confirmed;
                }
            }
            case Confirmed -> {
            }
        }
    }
}
