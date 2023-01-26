package com.example.wallet.domain.program;

import com.example.wallet.domain.entity.EthWallet;
import com.example.wallet.domain.entity.Transaction;
import com.example.wallet.domain.entity.TransactionStatus;
import com.example.wallet.domain.entity.event.Deposited;
import com.example.wallet.domain.entity.event.Withdrawn;
import com.example.wallet.domain.exception.InsufficientBalance;
import com.example.wallet.domain.exception.InvalidState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EthWalletProgramTest {
    private LocalDateTime currentDateTime = LocalDateTime.MIN;
    private long eventId = 0L;

    private final EthWalletProgram sut = new EthWalletProgram(() -> currentDateTime, () -> eventId);

    @Test
    void assert_fail_when_insufficient_balance() {
        String addressFrom = "from";
        String addressTo = "to";
        String secret = "secret";
        BigInteger balance = BigInteger.ONE;
        BigInteger amount = BigInteger.TEN;
        EthWallet wallet = new EthWallet(addressFrom, secret, balance);

        Assertions.assertThrows(InsufficientBalance.class, () -> sut.assertWithdraw(wallet, addressTo, amount, List.of()));
    }

    @Test
    void assert_fail_when_same_src_and_dst() {
        String addressFrom = "from";
        String addressTo = "from";
        String secret = "secret";
        BigInteger balance = BigInteger.TEN;
        BigInteger amount = BigInteger.TEN;
        EthWallet wallet = new EthWallet(addressFrom, secret, balance);

        Assertions.assertThrows(InvalidState.class, () -> sut.assertWithdraw(wallet, addressTo, amount, List.of()));
    }

    @Test
    void lift_withdrawn_event_only_with_confirmed_transaction() {
        String addressFrom = "from";
        String addressTo = "to";
        String transactionId = "tx";
        String secret = "secret";
        BigInteger balance = BigInteger.TEN;
        BigInteger amount = BigInteger.ONE;
        EthWallet wallet = new EthWallet(addressFrom, secret, balance);
        Transaction tx = new Transaction(transactionId, addressFrom, addressTo, 12, amount, TransactionStatus.Mined);

        Assertions.assertThrows(InvalidState.class, () -> sut.withdraw(wallet, tx));
    }

    @Test
    void lift_withdrawn_event_only_with_valid_address() {
        String addressFrom = "from";
        String addressTo = "to";
        String transactionId = "tx";
        String secret = "secret";
        BigInteger balance = BigInteger.TEN;
        BigInteger amount = BigInteger.ONE;
        EthWallet wallet = new EthWallet(addressFrom, secret, balance);
        Transaction tx = new Transaction(transactionId, addressTo, addressFrom, 12, amount, TransactionStatus.Confirmed);

        Assertions.assertThrows(InvalidState.class, () -> sut.withdraw(wallet, tx));
    }

    @Test
    void lift_withdrawn_event() {
        String addressFrom = "from";
        String addressTo = "to";
        String transactionId = "tx";
        String secret = "secret";
        BigInteger balance = BigInteger.TEN;
        BigInteger amount = BigInteger.ONE;
        EthWallet wallet = new EthWallet(addressFrom, secret, balance);
        Transaction tx = new Transaction(transactionId, addressFrom, addressTo, 12, amount, TransactionStatus.Confirmed);

        Result<EthWallet> result = sut.withdraw(wallet, tx);
        assertTrue(result.events.contains(new Withdrawn(eventId, wallet.address(), amount, currentDateTime)));
        assertEquals(wallet.withdrawn(amount), result.value);
    }

    @Test
    void lift_deposited_event() {
        String addressFrom = "from";
        String addressTo = "to";
        String transactionId = "tx";
        String secret = "secret";
        BigInteger balance = BigInteger.TEN;
        BigInteger amount = BigInteger.ONE;
        EthWallet wallet = new EthWallet(addressTo, secret, balance);
        Transaction tx = new Transaction(transactionId, addressFrom, addressTo, 12, amount, TransactionStatus.Confirmed);

        Result<EthWallet> result = sut.deposit(wallet, tx);
        assertTrue(result.events.contains(new Deposited(eventId, wallet.address(), amount, currentDateTime)));
        assertEquals(wallet.deposited(amount), result.value);
    }
}