package com.example.wallet.domain.entities.event;

import com.example.wallet.domain.DomainEventHandler;

import java.time.LocalDateTime;

public record TransactionConfirmed(long eventId, LocalDateTime occurredAt, String transactionId,
                                   int confirmedCount) implements TransactionEvent {
    @Override
    public <T> void accept(DomainEventHandler<T> handler) {
        handler.handle(this);
    }
}
