package com.example.wallet.domain.entity.event;

import com.example.wallet.domain.DomainEventHandler;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record TransactionStarted(long eventId, String transactionId, String srcAddress, String dstAddress,
                                 BigInteger amount, LocalDateTime occurredAt) implements TransactionEvent {
    @Override
    public <T> void accept(DomainEventHandler<T> handler) {
        handler.handle(this);
    }
}
