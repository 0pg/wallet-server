package com.example.wallet.domain.entities.event;

import com.example.wallet.domain.DomainEventHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public final class TransactionRollback implements TransactionEvent {
    private final long eventId;
    private final LocalDateTime occurredAt;
    private final String transactionId;
    private final BigInteger withdrawAmount;

    @Override
    public <T> void accept(DomainEventHandler<T> handler) {
        handler.handle(this);
    }
}
