package com.example.wallet.domain.logic;

import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.domain.entities.TransactionStatus;

public class TransactionPrograms {
    private static final int BLOCK_COUNT_FOR_CONFIRMATION = 12;


    public Transaction commit(Transaction tx) {
        int confirmationCount = Math.min(tx.confirmationCount() + 1, BLOCK_COUNT_FOR_CONFIRMATION);
        switch (tx.status()) {
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
