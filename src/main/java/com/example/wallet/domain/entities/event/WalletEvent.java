package com.example.wallet.domain.entities.event;

public sealed interface WalletEvent extends DomainEvent permits Deposited, TransactionRequested, WalletCreated, Withdrawn {
    String walletAddress();
}
