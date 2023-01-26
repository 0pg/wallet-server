package com.example.wallet.domain.event;

public interface TransactionEvent extends DomainEvent {
    String walletId();
    String hash();
}
