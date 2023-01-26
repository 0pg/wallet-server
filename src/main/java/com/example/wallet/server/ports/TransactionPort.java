package com.example.wallet.domain.ports;

import com.example.wallet.domain.entities.Transaction;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface TransactionPort {
    List<Transaction> getAllTransactions(String walletAddress);

    List<Transaction> getOngoingTransactions(String walletAddress);

    Optional<Transaction> findTransaction(String transactionId);

    BigInteger getTransactionCount(String walletAddress);

    void createTransaction(Transaction tx);

    void updateTransaction(Transaction tx);
}
