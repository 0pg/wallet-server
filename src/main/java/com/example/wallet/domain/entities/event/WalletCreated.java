package com.example.wallet.domain.entities.event;

import com.example.wallet.domain.DomainEventHandler;

import java.time.LocalDateTime;

public record WalletCreated(
        long eventId,
        String walletAddress,
        String secret,
        LocalDateTime occurredAt) implements WalletEvent {

    public void accept(DomainEventHandler handler) {
        handler.handle(this);
    }
}
