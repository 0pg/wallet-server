package com.example.wallet.domain.logic;

import com.example.wallet.domain.entities.EthWallet;
import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.domain.entities.event.DomainEvent;
import com.example.wallet.domain.entities.event.WithdrawStarted;
import com.example.wallet.domain.repositories.EthWalletRepository;
import com.example.wallet.domain.repositories.TransactionRepository;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class EthWalletPrograms {
    private EthWalletRepository walletRepository;
    private TransactionRepository transactionRepository;

    public CompletableFuture<DomainEvent> startWithdraw(@NonNull EthWallet wallet, @NonNull String to, @NonNull BigInteger amount) {
        return getPendingBalance(wallet)
                .thenCompose(pendingBalance -> {
                    if (wallet.balance().subtract(pendingBalance).longValue() < amount.longValue()) {
                        return CompletableFuture.failedFuture(new RuntimeException());
                    } else {
                        return transactionRepository.findTransactionCount(wallet.address())
                                .thenApply(nonce -> new WithdrawStarted(wallet.address(), nonce, to, amount, LocalDateTime.now()));
                    }
                });
    }

    private CompletableFuture<BigInteger> getPendingBalance(@NotNull EthWallet wallet) {
        return transactionRepository.getAllTransactions(wallet.address())
                .thenApply(transactions -> transactions.stream()
                        .filter(tx -> tx.status().isPending())
                        .map(Transaction::withdrawAmount)
                        .reduce(BigInteger.ZERO, BigInteger::add));
    }


}
