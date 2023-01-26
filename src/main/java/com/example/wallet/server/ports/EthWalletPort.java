package com.example.wallet.domain.ports;

import com.example.wallet.domain.entities.EthWallet;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface EthWalletPort {
    Optional<EthWallet> findWallet(String address);

    void createWallet(EthWallet wallet);

    void updateWallet(EthWallet wallet);
}
