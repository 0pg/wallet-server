package com.example.wallet.server.service;

import com.example.wallet.domain.programs.TransactionProgram;
import com.example.wallet.server.ports.TransactionPort;

public class TransactionService {
    private final TransactionProgram transactionProgram;
    private final TransactionPort transactionPort;

    public TransactionService(TransactionProgram transactionProgram, TransactionPort transactionPort) {
        this.transactionProgram = transactionProgram;
        this.transactionPort = transactionPort;
    }
}
