package com.example.wallet.domain.entities.event;

import com.example.wallet.domain.DomainEventHandler;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record TransactionRollback(long eventId, LocalDateTime occurredAt, String transactionId,
                                  BigInteger withdrawAmount) implements TransactionEvent {
    @Override
    public <T> void accept(DomainEventHandler<T> handler) {
        handler.handle(this);
    }
}
