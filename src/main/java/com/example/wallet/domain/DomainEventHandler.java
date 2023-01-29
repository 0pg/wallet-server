package com.example.wallet.domain;

import com.example.wallet.domain.entities.event.*;

public interface DomainEventHandler {
    void handle(TransactionConfirmed event);

    void handle(TransactionCommitted event);

    void handle(TransactionRollback event);

    void handle(TransactionStarted event);

    void handle(Deposited event);

    void handle(Withdrawn event);

    void handle(WalletCreated event);
}
