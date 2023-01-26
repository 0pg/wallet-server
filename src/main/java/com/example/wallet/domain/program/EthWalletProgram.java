package com.example.wallet.domain.program;

import com.example.wallet.domain.entity.EthWallet;
import com.example.wallet.domain.entity.Transaction;
import com.example.wallet.domain.entity.TransactionStatus;
import com.example.wallet.domain.entity.event.Deposited;
import com.example.wallet.domain.entity.event.WalletCreated;
import com.example.wallet.domain.entity.event.Withdrawn;
import com.example.wallet.domain.exception.InvalidState;
import com.example.wallet.domain.exception.Unexpected;
import com.example.wallet.domain.exception.WalletCreationFailed;
import com.example.wallet.domain.exception.InsufficientBalance;
import lombok.NonNull;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;

public class EthWalletProgram {
    private final Callable<LocalDateTime> timestampProvider;
    private final Callable<Long> eventIdProvider;
    private final EthWalletTransitionProgram transitionProgram;

    public EthWalletProgram(@NonNull Callable<LocalDateTime> timestampProvider, @NonNull Callable<Long> eventIdProvider) {
        this.timestampProvider = timestampProvider;
        this.eventIdProvider = eventIdProvider;
        this.transitionProgram = new EthWalletTransitionProgram();
    }

    public Result<EthWallet> createWallet(@NonNull String address, @NonNull String secret) {
        try {
            Result.Builder<EthWallet> builder = Result.builder(transitionProgram);
            LocalDateTime currentDateTime = currentDateTime();
            builder.addEvent(new WalletCreated(generateEventId(), address, secret, currentDateTime));
            EthWallet wallet = EthWallet.create(address, secret);
            return builder.build(wallet);
        } catch (Exception e) {
            throw new WalletCreationFailed(e, address);
        }
    }

    public Result<Void> assertWithdraw(@NonNull EthWallet wallet, @NonNull String dstAddress, @NonNull BigInteger amount, @NonNull List<Transaction> ongoingTransactions) {
        assertWithdrawable(wallet, dstAddress, amount, ongoingTransactions);
        return Result.empty();
    }

    public Result<EthWallet> withdraw(@NonNull EthWallet wallet, @NonNull Transaction tx) {
        if (tx.status() != TransactionStatus.Confirmed) {
            throw new InvalidState("withdraw", "transaction status " + tx.status().name());
        }

        if (wallet.address().equals(tx.srcAddress()) == false) {
            throw new InvalidState("withdraw", "different tx and wallet address " + wallet.address() + "," + tx.srcAddress());
        }

        Result.Builder<EthWallet> builder = Result.builder(transitionProgram);
        BigInteger amount = tx.amount();
        builder.addEvent(new Withdrawn(generateEventId(), wallet.address(), amount, currentDateTime()));
        return builder.build(wallet);
    }

    public Result<EthWallet> deposit(@NonNull EthWallet wallet, @NonNull Transaction tx) {
        if (tx.status() != TransactionStatus.Confirmed) {
            throw new InvalidState("deposit", "transaction status " + tx.status().name());
        }

        if (wallet.address().equals(tx.dstAddress()) == false) {
            throw new InvalidState("withdraw", "different tx and wallet address " + wallet.address() + "," + tx.dstAddress());
        }

        Result.Builder<EthWallet> builder = Result.builder(transitionProgram);
        BigInteger amount = tx.amount();
        builder.addEvent(new Deposited(generateEventId(), wallet.address(), amount, currentDateTime()));
        return builder.build(wallet);
    }

    private void assertWithdrawable(
            EthWallet wallet,
            String to,
            BigInteger amount,
            List<Transaction> ongoingTransactions) {
        if (amount.longValue() <= 0L) {
            throw new InvalidState("withdrawable", "amount " + amount);
        }

        if (wallet.address().equals(to)) {
            throw new InvalidState("withdrawable", "same src and dst address" + to);
        }

        assertWithdrawBalance(wallet, amount, ongoingTransactions);
    }

    private void assertWithdrawBalance(EthWallet wallet, BigInteger amount, List<Transaction> transactions) {
        BigInteger pendingWithdraw = getOngoingBalance(transactions);
        BigInteger balance = wallet.balance().subtract(pendingWithdraw);
        if (balance.longValue() < amount.longValue()) {
            throw new InsufficientBalance(balance, amount);
        }
    }

    private BigInteger getOngoingBalance(List<Transaction> transactions) {
        return transactions.stream()
                .filter(tx -> tx.status().isOngoing())
                .map(Transaction::amount)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    private LocalDateTime currentDateTime() {
        try {
            return timestampProvider.call();
        } catch (Exception e) {
            throw new Unexpected(e, "timestampProvider");
        }
    }

    private long generateEventId() {
        try {
            return eventIdProvider.call();
        } catch (Exception e) {
            throw new Unexpected(e, "eventIdProvider");
        }
    }
}
