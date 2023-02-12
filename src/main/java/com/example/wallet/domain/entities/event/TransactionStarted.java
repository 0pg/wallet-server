package com.example.wallet.domain.entities.event;

import com.example.wallet.domain.DomainEventHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public final class TransactionStarted implements TransactionEvent {
    private final long eventId;
    private final String transactionId;
    private final String srcAddress;
    private final String dstAddress;
    private final BigInteger amount;
    private final LocalDateTime occurredAt;

    @Override
    public <T> void accept(DomainEventHandler<T> handler) {
        handler.handle(this);
    }
}
