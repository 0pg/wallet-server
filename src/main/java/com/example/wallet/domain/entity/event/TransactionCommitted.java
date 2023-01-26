package com.example.wallet.domain.entity.event;

import com.example.wallet.domain.DomainEventHandler;

import java.time.LocalDateTime;

public record TransactionCommitted(long eventId, LocalDateTime occurredAt, String transactionId,
                                   int committedCount) implements TransactionEvent {
    @Override
    public <T> void accept(DomainEventHandler<T> handler) {
        handler.handle(this);
    }
}
