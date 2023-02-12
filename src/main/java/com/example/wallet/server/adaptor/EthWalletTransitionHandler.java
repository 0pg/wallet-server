package com.example.wallet.server.adaptor;

import com.example.wallet.domain.DomainEventHandler;
import com.example.wallet.domain.entities.EthWallet;
import com.example.wallet.domain.entities.event.*;
import com.example.wallet.server.mapper.EthWalletMapper;
import com.example.wallet.server.ports.EthWalletPort;

import java.util.Optional;

public class EthWalletTransitionHandler implements DomainEventHandler<Optional<EthWallet>> {

    private final EthWalletPort walletPort;

    public EthWalletTransitionHandler(EthWalletPort walletPort) {
        this.walletPort = walletPort;
    }

    @Override
    public Optional<EthWallet> handle(TransactionConfirmed event) {
        return Optional.empty();
    }

    @Override
    public Optional<EthWallet> handle(TransactionCommitted event) {
        return Optional.empty();
    }

    @Override
    public Optional<EthWallet> handle(TransactionRollback event) {
        return Optional.empty();
    }

    @Override
    public Optional<EthWallet> handle(TransactionStarted event) {
        return Optional.empty();
    }

    @Override
    public Optional<EthWallet> handle(Deposited event) {
        return Optional.of(getWallet(event.getWalletAddress()).deposited(event.getAmount()));
    }

    @Override
    public Optional<EthWallet> handle(Withdrawn event) {
        return Optional.of(getWallet(event.getWalletAddress()).withdrawn(event.getAmount()));
    }

    @Override
    public Optional<EthWallet> handle(WalletCreated event) {
        return Optional.of(EthWalletMapper.INSTANCE.fromCreatedEvent(event));
    }

    private EthWallet getWallet(String address) {
        return EthWalletMapper.INSTANCE.toEntity(
                walletPort.findById(address)
                        .orElseThrow(() -> new RuntimeException())
        );
    }
}
