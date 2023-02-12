package com.example.wallet.domain.entities.event;

public sealed interface WalletEvent extends DomainEvent permits Deposited, WalletCreated, Withdrawn {
    String getWalletAddress();
}
