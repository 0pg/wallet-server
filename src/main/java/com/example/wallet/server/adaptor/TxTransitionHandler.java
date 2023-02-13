package com.example.wallet.server.adaptor;

import com.example.wallet.domain.DomainEventHandler;
import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.domain.entities.event.*;
import com.example.wallet.domain.programs.TxTransitionProgram;
import com.example.wallet.server.exception.DataNotFound;
import com.example.wallet.server.mapper.TransactionMapper;
import com.example.wallet.server.ports.TransactionPort;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TxTransitionHandler implements DomainEventHandler<Optional<Transaction>> {

    private final TransactionPort transactionPort;
    private final TxTransitionProgram transitionProgram;

    public TxTransitionHandler(@NonNull TransactionPort transactionPort, TxTransitionProgram transitionProgram) {
        this.transactionPort = transactionPort;
        this.transitionProgram = transitionProgram;
    }

    @Override
    public Optional<Transaction> handle(TransactionConfirmed event) {
        Transaction tx = getTransaction(event.transactionId());
        return Optional.of(transitionProgram.handle(event).apply(tx));
    }

    @Override
    public Optional<Transaction> handle(TransactionCommitted event) {
        Transaction tx = getTransaction(event.transactionId());
        return Optional.of(transitionProgram.handle(event).apply(tx));
    }

    @Override
    public Optional<Transaction> handle(TransactionRollback event) {
        Transaction tx = getTransaction(event.transactionId());
        return Optional.of(transitionProgram.handle(event).apply(tx));
    }

    @Override
    public Optional<Transaction> handle(TransactionStarted event) {
        return Optional.of(transitionProgram.handle(event).apply(Transaction.create(event.transactionId(), event.srcAddress(), event.dstAddress(), event.amount())));
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
                transactionPort.findById(txId).orElseThrow(() -> new DataNotFound("transaction", txId))
        );
    }
}
