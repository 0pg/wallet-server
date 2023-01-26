package com.example.wallet.domain.entities.event;

import com.example.wallet.domain.DomainEventHandler;

import java.time.LocalDateTime;

public record TransactionStarted(
        long eventId,
        String transactionId,
        String srcAddress,
        String dstAddress,
        LocalDateTime occurredAt
) implements TransactionEvent {
    @Override
    public void accept(DomainEventHandler handler) {
        handler.handle(this);
    }
}
