package com.example.wallet.domain;

import com.example.wallet.domain.entities.event.*;

public interface DomainEventHandler<T> {
    T handle(TransactionConfirmed event);

    T handle(TransactionCommitted event);

    T handle(TransactionRollback event);

    T handle(TransactionStarted event);

    T handle(Deposited event);

    T handle(Withdrawn event);

    T handle(WalletCreated event);
}
