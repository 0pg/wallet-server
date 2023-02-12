package com.example.wallet.server.adaptor;

import com.example.wallet.domain.DomainEventHandler;
import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.domain.entities.TransactionStatus;
import com.example.wallet.domain.entities.event.*;
import com.example.wallet.server.mapper.TransactionMapper;
import com.example.wallet.server.ports.TransactionPort;
import lombok.NonNull;

import java.util.Optional;

public class TxTransitionHandler implements DomainEventHandler<Optional<Transaction>> {

    private final TransactionPort transactionPort;

    public TxTransitionHandler(@NonNull TransactionPort transactionPort) {
        this.transactionPort = transactionPort;
    }

    @Override
    public Optional<Transaction> handle(TransactionConfirmed event) {
        Transaction tx = getTransaction(event.getTransactionId());
        return Optional.of(tx.confirmed(event.getConfirmedCount()));
    }

    @Override
    public Optional<Transaction> handle(TransactionCommitted event) {
        Transaction tx = getTransaction(event.getTransactionId());
        return Optional.of(tx.committed(event.getCount()));
    }

    @Override
    public Optional<Transaction> handle(TransactionRollback event) {
        Transaction tx = getTransaction(event.getTransactionId());
        return Optional.of(tx.statusUpdated(TransactionStatus.Failed));
    }

    @Override
    public Optional<Transaction> handle(TransactionStarted event) {
        return Optional.of(TransactionMapper.INSTANCE.fromStartedEvent(event));
    }

    @Override
    public Optional<Transaction> handle(Deposited event) {
        return Optional.empty();
    }

    @Override
    public Optional<Transaction> handle(Withdrawn event) {
        return Optional.empty();
    }

    @Override
    public Optional<Transaction> handle(WalletCreated event) {
        return Optional.empty();
    }

    private Transaction getTransaction(String txId) {
        return TransactionMapper.INSTANCE.toEntity(
                transactionPort.findById(txId).orElseThrow(() -> new RuntimeException())
        );
    }
}
