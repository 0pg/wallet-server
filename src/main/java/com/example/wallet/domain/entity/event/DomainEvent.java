package com.example.wallet.domain.entity.event;

import com.example.wallet.domain.DomainEventHandler;

import java.time.LocalDateTime;

public sealed interface DomainEvent permits TransactionEvent, WalletEvent {
    long eventId();

    LocalDateTime occurredAt();

    <T> void accept(DomainEventHandler<T> handler);
}
