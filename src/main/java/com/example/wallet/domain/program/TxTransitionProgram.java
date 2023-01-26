package com.example.wallet.domain.program;

import com.example.wallet.domain.DomainEventHandler;
import com.example.wallet.domain.entity.Transaction;
import com.example.wallet.domain.entity.TransactionStatus;
import com.example.wallet.domain.entity.event.*;

import java.util.function.Function;

public class TxTransitionProgram implements DomainEventHandler<Function<Transaction, Transaction>> {
    @Override
    public Function<Transaction, Transaction> handle(TransactionConfirmed event) {
        return (Transaction tx) -> tx.confirmed(event.confirmedCount());
    }

    @Override
    public Function<Transaction, Transaction> handle(TransactionCommitted event) {
        return (Transaction tx) -> tx.committed(event.committedCount());
    }

    @Override
    public Function<Transaction, Transaction> handle(TransactionRollback event) {
        return (Transaction tx) -> tx.statusUpdated(TransactionStatus.Failed);
    }

    @Override
    public Function<Transaction, Transaction> handle(TransactionStarted event) {
        return Function.identity();
    }

    @Override
    public Function<Transaction, Transaction> handle(Deposited event) {
        return Function.identity();
    }

    @Override
    public Function<Transaction, Transaction> handle(Withdrawn event) {
        return Function.identity();
    }

    @Override
    public Function<Transaction, Transaction> handle(WalletCreated event) {
        return Function.identity();
    }
}
