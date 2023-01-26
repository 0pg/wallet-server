package com.example.wallet.domain.entities.event;

import com.example.wallet.domain.ports.WalletEventVisitor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public record WithdrawStarted(
        long eventId,
        String walletAddress,
        BigInteger nonce,
        String destinationAddress,
        BigInteger amount,
        LocalDateTime occurredAt

        ) implements WalletEvent {
        @Override
        public CompletableFuture<Void> accept(WalletEventVisitor visitor) {
                return visitor.visit(this);
        }
}
