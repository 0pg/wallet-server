package com.example.wallet.domain.exception;

public class WalletCreationFailed extends DomainException {
    public WalletCreationFailed(Throwable cause, String walletAddress) {
        super(cause, walletAddress);
    }
}
