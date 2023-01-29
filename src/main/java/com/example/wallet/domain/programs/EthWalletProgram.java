package com.example.wallet.domain.programs;

import com.example.wallet.domain.entities.EthWallet;
import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.domain.entities.TransactionStatus;
import com.example.wallet.domain.entities.event.Deposited;
import com.example.wallet.domain.entities.event.WalletCreated;
import com.example.wallet.domain.entities.event.Withdrawn;
import lombok.NonNull;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;

public class EthWalletProgram {
    private final Callable<LocalDateTime> timestampProvider;
    private final Callable<Long> eventIdProvider;

    public EthWalletProgram(@NonNull Callable<LocalDateTime> timestampProvider, @NonNull Callable<Long> eventIdProvider) {
        this.timestampProvider = timestampProvider;
        this.eventIdProvider = eventIdProvider;
    }

    public Result<EthWallet> createWallet(@NonNull String address, @NonNull String secret) {
        try {
            Result.Builder<EthWallet> builder = Result.builder();
            LocalDateTime currentDateTime = currentDateTime();
            builder.addEvent(new WalletCreated(generateEventId(), address, secret, currentDateTime));
            EthWallet wallet = new EthWallet(address);
            return builder.build(wallet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Result<Void> assertWithdraw(@NonNull EthWallet wallet, @NonNull String dstAddress, @NonNull BigInteger amount, @NonNull List<Transaction> ongoingTransactions) {
        assertWithdrawable(wallet, dstAddress, amount, ongoingTransactions);
        return Result.empty();
    }

    public Result<EthWallet> withdraw(@NonNull EthWallet wallet, @NonNull Transaction tx) {
        if (tx.status() != TransactionStatus.Confirmed) {
            throw new RuntimeException();
        }

        if (wallet.address().equals(tx.srcAddress()) == false) {
            throw new RuntimeException();
        }

        Result.Builder<EthWallet> builder = Result.builder();
        BigInteger amount = tx.amount();
        builder.addEvent(new Withdrawn(generateEventId(), wallet.address(), amount, currentDateTime()));
        return builder.build(wallet.withdrawn(amount));
    }

    public Result<EthWallet> deposit(@NonNull EthWallet wallet, @NonNull Transaction tx) {
        if (tx.status() != TransactionStatus.Confirmed) {
            throw new RuntimeException();
        }

        if (wallet.address().equals(tx.dstAddress()) == false) {
            throw new RuntimeException();
        }

        Result.Builder<EthWallet> builder = Result.builder();
        BigInteger amount = tx.amount();
        builder.addEvent(new Deposited(generateEventId(), wallet.address(), amount, currentDateTime()));
        return builder.build(wallet.deposited(amount));
    }

    private void assertWithdrawable(
            EthWallet wallet,
            String to,
            BigInteger amount,
            List<Transaction> ongoingTransactions) {
        if (amount.longValue() <= 0L) {
            throw new RuntimeException();
        }

        if (wallet.address().equals(to)) {
            throw new RuntimeException();
        }

        assertWithdrawBalance(wallet, amount, ongoingTransactions);
    }

    private void assertWithdrawBalance(EthWallet wallet, BigInteger amount, List<Transaction> transactions) {
        BigInteger pendingWithdraw = getPendingBalance(transactions);

        if (wallet.balance().subtract(pendingWithdraw).longValue() < amount.longValue()) {
            throw new RuntimeException();
        }
    }

    private BigInteger getPendingBalance(List<Transaction> transactions) {
        return transactions.stream()
                .filter(tx -> tx.status().isOngoing())
                .map(Transaction::amount)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    private LocalDateTime currentDateTime() {
        try {
            return timestampProvider.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private long generateEventId() {
        try {
            return eventIdProvider.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
