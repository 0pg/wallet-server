package com.example.wallet.domain.programs;

import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.domain.entities.TransactionStatus;
import com.example.wallet.domain.entities.event.*;
import lombok.NonNull;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class TransactionProgram {
    private static final int BLOCK_COUNT_FOR_CONFIRMATION = 12;

    private final Callable<LocalDateTime> timestampProvider;
    private final Callable<Long> eventIdProvider;

    public TransactionProgram(@NonNull Callable<LocalDateTime> timestampProvider, @NonNull Callable<Long> eventIdProvider) {
        this.timestampProvider = timestampProvider;
        this.eventIdProvider = eventIdProvider;
    }

    public Result<Transaction> createTransaction(@NonNull String transactionId, @NonNull String srcAddress, @NonNull String dstAddress, @NonNull BigInteger amount) {
        Result.Builder<Transaction> builder = Result.builder();
        Transaction tx = new Transaction(transactionId, srcAddress, dstAddress, amount);
        return builder.addEvent(new TransactionStarted(generateEventId(), transactionId, srcAddress, dstAddress, amount, currentDateTime())).build(tx);
    }

    public Result<Transaction> commit(@NonNull Transaction tx, int count) {
        int confirmationCount = Math.min(count, BLOCK_COUNT_FOR_CONFIRMATION - tx.confirmationCount());
        Transaction updated = tx.confirmed(confirmationCount);

        return switch (updated.status()) {
            case Pending -> {
                if (updated.confirmationCount() < BLOCK_COUNT_FOR_CONFIRMATION) {
                    yield acceptTransaction(updated, count);
                } else {
                    yield confirmTransaction(updated);
                }
            }
            case Mined -> {
                if (updated.confirmationCount() == BLOCK_COUNT_FOR_CONFIRMATION) {
                    yield confirmTransaction(updated);
                } else {
                    yield commitTransaction(updated, count);
                }
            }
            case Confirmed -> new Result<>(updated);
            case Failed -> throw new RuntimeException();
        };
    }

    private Result<Transaction> acceptTransaction(Transaction tx, int count) {
        Result.Builder<Transaction> builder = Result.builder();
        long eventId = generateEventId();
        LocalDateTime currentDateTime = currentDateTime();

        return builder
                .addEvent(new TransactionCommitted(eventId, currentDateTime, tx.id(), count))
                .build(tx.statusUpdated(TransactionStatus.Mined));
    }

    private Result<Transaction> commitTransaction(Transaction tx, int count) {
        Result.Builder<Transaction> builder = Result.builder();
        long eventId = generateEventId();
        LocalDateTime currentDateTime = currentDateTime();

        return builder
                .addEvent(new TransactionCommitted(eventId, currentDateTime, tx.id(), count))
                .build(tx);
    }

    private Result<Transaction> confirmTransaction(Transaction tx) {
        Result.Builder<Transaction> builder = Result.builder();
        long eventId = generateEventId();
        LocalDateTime currentDateTime = currentDateTime();

        return builder
                .addEvent(new TransactionConfirmed(eventId, currentDateTime, tx.id()))
                .addEvent(new Withdrawn(eventId, tx.srcAddress(), tx.amount(), currentDateTime))
                .addEvent(new Deposited(eventId, tx.dstAddress(), tx.amount(), currentDateTime))
                .build(tx.statusUpdated(TransactionStatus.Confirmed));
    }

    public Result<Transaction> rollback(@NonNull Transaction tx) {
        Result.Builder<Transaction> builder = Result.builder();
        return builder
                .addEvent(new TransactionRollback(generateEventId(), currentDateTime(), tx.id(), tx.amount()))
                .build(tx.statusUpdated(TransactionStatus.Failed));
    }

    private LocalDateTime currentDateTime() {
        try {
            return timestampProvider.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private long generateEventId() {
        try {
            return eventIdProvider.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
