package com.example.wallet.domain.entities.event;

import com.example.wallet.domain.DomainEventHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public final class WalletCreated implements WalletEvent {
    private final long eventId;
    private final String walletAddress;
    private final String secret;
    private final LocalDateTime occurredAt;

    @Override
    public void accept(DomainEventHandler handler) {
        handler.handle(this);
    }
}
