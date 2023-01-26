package com.example.wallet.domain.entities.event;

import com.example.wallet.domain.DomainEventHandler;

import java.time.LocalDateTime;

public record TransactionConfirmed(
        long eventId,
        String srcAddress,

        String dstAddress,
        LocalDateTime occurredAt,
        String transactionId
) implements TransactionEvent {
    @Override
    public void accept(DomainEventHandler handler) {
        handler.handle(this);
    }
}
