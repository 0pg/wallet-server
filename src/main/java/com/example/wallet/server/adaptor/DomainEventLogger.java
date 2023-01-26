package com.example.wallet.server.adaptor;

import com.example.wallet.domain.DomainEventHandler;
import com.example.wallet.domain.entity.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DomainEventLogger implements DomainEventHandler<Void> {
    public void logEvents(List<DomainEvent> events) {
        events.forEach(event -> event.accept(this));
    }

    @Override
    public Void handle(TransactionConfirmed event) {
        log.info(event.toString());
        return null;
    }

    @Override
    public Void handle(TransactionCommitted event) {
        log.info(event.toString());
        return null;
    }

    @Override
    public Void handle(TransactionRollback event) {
        log.info(event.toString());
        return null;
    }

    @Override
    public Void handle(TransactionStarted event) {
        log.info(event.toString());
        return null;
    }

    @Override
    public Void handle(Deposited event) {
        log.info(event.toString());
        return null;
    }

    @Override
    public Void handle(Withdrawn event) {
        log.info(event.toString());
        return null;
    }

    @Override
    public Void handle(WalletCreated event) {
        log.info(event.toString());
        return null;
    }
}
