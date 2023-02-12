package com.example.wallet.server.adaptor;

import com.example.wallet.domain.DomainEventHandler;
import com.example.wallet.domain.entities.EthWallet;
import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.domain.entities.event.*;
import com.example.wallet.server.mapper.EthWalletMapper;
import com.example.wallet.server.mapper.TransactionEventMapper;
import com.example.wallet.server.mapper.TransactionMapper;
import com.example.wallet.server.ports.EthWalletPort;
import com.example.wallet.server.ports.TransactionEventPort;
import com.example.wallet.server.ports.TransactionPort;
import lombok.NonNull;

public class PersistEventHandler implements DomainEventHandler<Void> {
    private final TransactionPort transactionPort;
    private final EthWalletPort walletPort;
    private final TransactionEventPort transactionEventPort;
    private final TxTransitionHandler txTransitionHandler;
    private final EthWalletTransitionHandler walletTransitionHandler;

    public PersistEventHandler(@NonNull TransactionPort transactionPort,
                               @NonNull EthWalletPort walletPort,
                               @NonNull TransactionEventPort transactionEventPort,
                               @NonNull TxTransitionHandler txTransitionHandler,
                               @NonNull EthWalletTransitionHandler walletTransitionHandler) {
        this.transactionPort = transactionPort;
        this.walletPort = walletPort;
        this.transactionEventPort = transactionEventPort;
        this.txTransitionHandler = txTransitionHandler;
        this.walletTransitionHandler = walletTransitionHandler;
    }


    private EthWallet getWallet(String address) {
        return EthWalletMapper.INSTANCE.toEntity(
                walletPort.findById(address).orElseThrow(() -> new RuntimeException())
        );
    }

    private Transaction getTransaction(String transactionId) {
        return TransactionMapper.INSTANCE.toEntity(
                transactionPort.findById(transactionId).orElseThrow(() -> new RuntimeException())
        );
    }

    @Override
    public Void handle(TransactionConfirmed event) {
        transactionEventPort.save(TransactionEventMapper.INSTANCE.fromConfirmed(event));
        txTransitionHandler.handle(event)
                .map(TransactionMapper.INSTANCE::toDTO)
                .ifPresent(transactionPort::save);
        return null;
    }

    @Override
    public Void handle(TransactionCommitted event) {
        transactionEventPort.save(TransactionEventMapper.INSTANCE.fromCommitted(event));
        txTransitionHandler.handle(event)
                .map(TransactionMapper.INSTANCE::toDTO)
                .ifPresent(transactionPort::save);
        return null;
    }

    @Override
    public Void handle(TransactionRollback event) {
        transactionEventPort.save(TransactionEventMapper.INSTANCE.fromRollback(event));
        txTransitionHandler.handle(event)
                .map(TransactionMapper.INSTANCE::toDTO)
                .ifPresent(transactionPort::save);
        return null;
    }

    @Override
    public Void handle(TransactionStarted event) {
        txTransitionHandler.handle(event)
                .map(TransactionMapper.INSTANCE::toDTO)
                .ifPresent(transactionPort::save);
        return null;
    }

    @Override
    public Void handle(Deposited event) {
        walletTransitionHandler.handle(event)
                .map(EthWalletMapper.INSTANCE::toDTO)
                .ifPresent(walletPort::save);
        return null;
    }

    @Override
    public Void handle(Withdrawn event) {
        walletTransitionHandler.handle(event)
                .map(EthWalletMapper.INSTANCE::toDTO)
                .ifPresent(walletPort::save);
        return null;
    }

    @Override
    public Void handle(WalletCreated event) {
        walletTransitionHandler.handle(event)
                .map(EthWalletMapper.INSTANCE::toDTO)
                .ifPresent(walletPort::save);
        return null;
    }
}
