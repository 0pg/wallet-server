package com.example.wallet.domain.entity.event;

public sealed interface TransactionEvent
        extends DomainEvent
        permits TransactionCommitted, TransactionConfirmed, TransactionRollback, TransactionStarted {
    String transactionId();
}
