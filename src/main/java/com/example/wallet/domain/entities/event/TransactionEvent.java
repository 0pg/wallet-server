package com.example.wallet.domain.entities.event;

public sealed interface TransactionEvent
        extends DomainEvent
        permits TransactionCommitted, TransactionConfirmed, TransactionMined, TransactionRollback, TransactionStarted {
    String transactionId();

    String srcAddress();
    String dstAddress();
}
