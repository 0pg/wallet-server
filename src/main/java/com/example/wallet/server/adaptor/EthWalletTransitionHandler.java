package com.example.wallet.server.adaptor;

import com.example.wallet.domain.DomainEventHandler;
import com.example.wallet.domain.entities.EthWallet;
import com.example.wallet.domain.entities.event.*;
import com.example.wallet.domain.programs.EthWalletTransitionProgram;
import com.example.wallet.server.mapper.EthWalletMapper;
import com.example.wallet.server.ports.EthWalletPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EthWalletTransitionHandler implements DomainEventHandler<Optional<EthWallet>> {

    private final EthWalletPort ethWalletPort;
    private final EthWalletTransitionProgram walletTransitionProgram;

    public EthWalletTransitionHandler(EthWalletPort ethWalletPort, EthWalletTransitionProgram walletTransitionProgram) {
        this.ethWalletPort = ethWalletPort;
        this.walletTransitionProgram = walletTransitionProgram;
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
        return getWallet(event.walletAddress())
                .map(wallet -> walletTransitionProgram.handle(event).apply(wallet));
    }

    @Override
    public Optional<EthWallet> handle(Withdrawn event) {
        return getWallet(event.walletAddress())
                .map(wallet -> walletTransitionProgram.handle(event).apply(wallet));
    }

    @Override
    public Optional<EthWallet> handle(WalletCreated event) {
        return Optional.of(walletTransitionProgram.handle(event).apply(EthWallet.create(event.walletAddress(), event.secret())));
    }

    private Optional<EthWallet> getWallet(String address) {
        return ethWalletPort.findById(address).map(EthWalletMapper.INSTANCE::toEntity);
    }
}
