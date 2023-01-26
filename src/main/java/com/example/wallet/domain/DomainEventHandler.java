package com.example.wallet.domain.programs;

import com.example.wallet.domain.entities.event.*;

public interface DomainEventProgram {
    void handle(TransactionConfirmed event);

    void handle(TransactionCommitted event);

    void handle(TransactionRollback event);

    void handle(TransactionMined event);

    void handle(TransactionStarted event);

    void handle(Deposited event);

    void handle(Withdrawn event);

    void handle(WalletCreated event);

    void handle(TransactionRequested event);
}
