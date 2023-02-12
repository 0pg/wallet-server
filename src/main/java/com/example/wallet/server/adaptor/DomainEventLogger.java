package com.example.wallet.server.adaptor;

import com.example.wallet.domain.DomainEventHandler;
import com.example.wallet.domain.entities.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DomainEventLogger implements DomainEventHandler<Void> {
    private final Logger logger;

    public DomainEventLogger() {
        this.logger = LoggerFactory.getLogger(DomainEventLogger.class);
    }

    public void logEvents(List<DomainEvent> events) {
        events.forEach(event -> event.accept(this));
    }

    @Override
    public Void handle(TransactionConfirmed event) {
        logger.info(event.toString());
        return null;
    }

    @Override
    public Void handle(TransactionCommitted event) {
        logger.info(event.toString());
        return null;
    }

    @Override
    public Void handle(TransactionRollback event) {
        logger.info(event.toString());
        return null;
    }

    @Override
    public Void handle(TransactionStarted event) {
        logger.info(event.toString());
        return null;
    }

    @Override
    public Void handle(Deposited event) {
        logger.info(event.toString());
        return null;
    }

    @Override
    public Void handle(Withdrawn event) {
        logger.info(event.toString());
        return null;
    }

    @Override
    public Void handle(WalletCreated event) {
        logger.info(event.toString());
        return null;
    }
}
