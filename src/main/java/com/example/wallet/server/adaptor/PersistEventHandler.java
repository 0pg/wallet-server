package com.example.wallet.server.adaptor;

import com.example.wallet.domain.DomainEventHandler;
import com.example.wallet.domain.entities.EthWallet;
import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.domain.entities.event.*;
import com.example.wallet.server.mapper.EthWalletMapper;
import com.example.wallet.server.mapper.TransactionMapper;
import com.example.wallet.server.ports.EthWalletPort;
import com.example.wallet.server.ports.TransactionEventPort;
import com.example.wallet.server.ports.TransactionPort;
import lombok.NonNull;

public class PersistEventHandler implements DomainEventHandler {
    private final TransactionPort transactionPort;
    private final EthWalletPort walletPort;
    private final TransactionEventPort transactionEventPort;

    public PersistEventHandler(@NonNull TransactionPort transactionPort, @NonNull EthWalletPort walletPort, @NonNull TransactionEventPort transactionEventPort) {
        this.transactionPort = transactionPort;
        this.walletPort = walletPort;
        this.transactionEventPort = transactionEventPort;
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
    public void handle(TransactionConfirmed event) {

    }

    @Override
    public void handle(TransactionCommitted event) {

    }

    @Override
    public void handle(TransactionRollback event) {

    }

    @Override
    public void handle(TransactionStarted event) {

    }

    @Override
    public void handle(Deposited event) {

    }

    @Override
    public void handle(Withdrawn event) {

    }

    @Override
    public void handle(WalletCreated event) {

    }
}
