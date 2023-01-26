package com.example.wallet.domain.entity.event;

import com.example.wallet.domain.DomainEventHandler;

import java.time.LocalDateTime;

public record WalletCreated(long eventId, String walletAddress, String secret,
                            LocalDateTime occurredAt) implements WalletEvent {
    @Override
    public <T> void accept(DomainEventHandler<T> handler) {
        handler.handle(this);
    }
}
