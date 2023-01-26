package com.example.wallet.domain.programs;

import com.example.wallet.domain.entities.EthWallet;
import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.domain.entities.TransactionStatus;
import com.example.wallet.domain.entities.event.Deposited;
import com.example.wallet.domain.entities.event.DomainEvent;
import com.example.wallet.domain.entities.event.TransactionRequested;
import com.example.wallet.domain.entities.event.Withdrawn;
import com.example.wallet.server.ports.EthWalletPort;
import com.example.wallet.server.ports.TransactionPort;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EthWalletProgramTest {
    private LocalDateTime currentDateTime = LocalDateTime.MIN;
    private long eventId = 0L;

    private final EthWalletProgram sut = new EthWalletProgram(() -> currentDateTime, () -> eventId);

    @Test
    void prepareWithdraw() {
        String addressFrom = "from";
        String addressTo = "to";
        BigInteger balance = BigInteger.TEN;
        BigInteger amount = BigInteger.ONE;
        EthWallet wallet = new EthWallet(addressFrom, balance);

        Result<EthWallet> result = sut.prepareWithdraw(wallet, addressTo, amount, List.of());
        assertTrue(result.events.contains(new TransactionRequested(eventId, wallet.address(), addressTo, amount, currentDateTime)));
        assertEquals(wallet, result.value);
    }

    @Test
    void withdrawn() {
        String addressFrom = "from";
        String addressTo = "to";
        String transactionId = "tx";
        BigInteger balance = BigInteger.TEN;
        BigInteger amount = BigInteger.ONE;
        EthWallet wallet = new EthWallet(addressFrom, balance);
        Transaction tx = new Transaction(transactionId, addressFrom, addressTo, 12, amount, TransactionStatus.Confirmed);

        Result<EthWallet> result = sut.withdraw(wallet, tx);
        assertTrue(result.events.contains(new Withdrawn(eventId, wallet.address(), amount, currentDateTime)));
        assertEquals(wallet.withdrawn(amount), result.value);
    }

    @Test
    void deposited() {
        String addressFrom = "from";
        String addressTo = "to";
        String transactionId = "tx";
        BigInteger balance = BigInteger.TEN;
        BigInteger amount = BigInteger.ONE;
        EthWallet wallet = new EthWallet(addressTo, balance);
        Transaction tx = new Transaction(transactionId, addressFrom, addressTo, 12, amount, TransactionStatus.Confirmed);

        Result<EthWallet> result = sut.deposit(wallet, tx);
        assertTrue(result.events.contains(new Deposited(eventId, wallet.address(), amount, currentDateTime)));
        assertEquals(wallet.deposited(amount), result.value);
    }
}