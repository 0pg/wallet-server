package com.example.wallet.domain.entities.event;

import com.example.wallet.domain.DomainEventHandler;

import java.time.LocalDateTime;

public sealed interface DomainEvent permits TransactionEvent, WalletEvent {
    long eventId();

    LocalDateTime occurredAt();

    void accept(DomainEventHandler handler);
}
