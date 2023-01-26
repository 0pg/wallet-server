package com.example.wallet.domain.entity.event;

public sealed interface WalletEvent extends DomainEvent permits Deposited, WalletCreated, Withdrawn {
    String walletAddress();
}
