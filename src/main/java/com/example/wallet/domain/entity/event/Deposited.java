package com.example.wallet.domain.entity.event;

import com.example.wallet.domain.DomainEventHandler;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record Deposited(long eventId, String walletAddress, BigInteger amount,
                        LocalDateTime occurredAt) implements WalletEvent {
    @Override
    public <T> void accept(DomainEventHandler<T> handler) {
        handler.handle(this);
    }
}
