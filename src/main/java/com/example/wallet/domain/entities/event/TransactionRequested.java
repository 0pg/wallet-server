package com.example.wallet.domain.entities.event;

import com.example.wallet.domain.DomainEventHandler;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record TransactionRequested(
        long eventId,
        String walletAddress,
        String detAddress,
        BigInteger amount,
        LocalDateTime occurredAt
) implements WalletEvent {
    @Override
    public void accept(DomainEventHandler handler) {
        handler.handle(this);
    }
}
