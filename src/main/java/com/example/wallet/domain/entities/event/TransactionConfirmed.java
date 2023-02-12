package com.example.wallet.domain.entities.event;

import com.example.wallet.domain.DomainEventHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public final class TransactionConfirmed implements TransactionEvent {
    private final long eventId;
    private final LocalDateTime occurredAt;
    private final String transactionId;

    @Override
    public void accept(DomainEventHandler handler) {
        handler.handle(this);
    }
}
