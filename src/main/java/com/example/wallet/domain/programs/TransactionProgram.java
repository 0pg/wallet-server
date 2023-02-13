package com.example.wallet.domain.programs;

import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.domain.entities.event.*;
import com.example.wallet.domain.exception.Unexpected;
import lombok.NonNull;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class TransactionProgram {
    private static final int BLOCK_COUNT_FOR_CONFIRMATION = 12;

    private final Callable<LocalDateTime> timestampProvider;
    private final Callable<Long> eventIdProvider;
    private final TxTransitionProgram transitionProgram;

    public TransactionProgram(@NonNull Callable<LocalDateTime> timestampProvider, @NonNull Callable<Long> eventIdProvider) {
        this.timestampProvider = timestampProvider;
        this.eventIdProvider = eventIdProvider;
        this.transitionProgram = new TxTransitionProgram();
    }

    public Result<Transaction> createTransaction(@NonNull String transactionId, @NonNull String srcAddress, @NonNull String dstAddress, @NonNull BigInteger amount) {
        Transaction tx = Transaction.create(transactionId, srcAddress, dstAddress, amount);
        Result.Builder<Transaction> builder = Result.builder(transitionProgram);
        return builder.addEvent(new TransactionStarted(generateEventId(), transactionId, srcAddress, dstAddress, amount, currentDateTime())).build(tx);
    }

    public Result<Transaction> commit(@NonNull Transaction tx, int count) {
        return switch (tx.status()) {
            case Pending -> {
                if (tx.confirmationCount() + count < BLOCK_COUNT_FOR_CONFIRMATION) {
                    yield acceptTransaction(tx, count);
                } else {
                    yield confirmTransaction(tx);
                }
            }
            case Mined -> {
                if (tx.confirmationCount() + count >= BLOCK_COUNT_FOR_CONFIRMATION) {
                    yield confirmTransaction(tx);
                } else {
                    yield commitTransaction(tx, count);
                }
            }
            case Confirmed -> new Result<>(tx);
            case Failed -> throw new RuntimeException();
        };
    }

    private Result<Transaction> acceptTransaction(Transaction tx, int count) {
        Result.Builder<Transaction> builder = Result.builder(transitionProgram);
        long eventId = generateEventId();
        LocalDateTime currentDateTime = currentDateTime();

        return builder
                .addEvent(new TransactionCommitted(eventId, currentDateTime, tx.id(), count))
                .build(tx);
    }

    private Result<Transaction> commitTransaction(Transaction tx, int count) {
        Result.Builder<Transaction> builder = Result.builder(transitionProgram);
        long eventId = generateEventId();
        LocalDateTime currentDateTime = currentDateTime();

        return builder
                .addEvent(new TransactionCommitted(eventId, currentDateTime, tx.id(), count))
                .build(tx);
    }

    private Result<Transaction> confirmTransaction(Transaction tx) {
        Result.Builder<Transaction> builder = Result.builder(transitionProgram);
        long eventId = generateEventId();
        LocalDateTime currentDateTime = currentDateTime();

        return builder
                .addEvent(new TransactionConfirmed(eventId, currentDateTime, tx.id(), BLOCK_COUNT_FOR_CONFIRMATION))
                .addEvent(new Withdrawn(eventId, tx.srcAddress(), tx.amount(), currentDateTime))
                .addEvent(new Deposited(eventId, tx.dstAddress(), tx.amount(), currentDateTime))
                .build(tx);
    }

    public Result<Transaction> rollback(@NonNull Transaction tx) {
        Result.Builder<Transaction> builder = Result.builder(transitionProgram);
        return builder
                .addEvent(new TransactionRollback(generateEventId(), currentDateTime(), tx.id(), tx.amount()))
                .build(tx);
    }

    private LocalDateTime currentDateTime() {
        try {
            return timestampProvider.call();
        } catch (Exception e) {
            throw new Unexpected(e, "timestampProvider");
        }
    }

    private long generateEventId() {
        try {
            return eventIdProvider.call();
        } catch (Exception e) {
            throw new Unexpected(e, "eventIdProvider");
        }
    }
}
