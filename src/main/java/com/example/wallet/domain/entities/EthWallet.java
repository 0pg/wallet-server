package com.example.wallet.domain.entities;

import java.math.BigInteger;

public record EthWallet(String address, BigInteger balance) {
    public static EthWallet create(String address) {
        return new EthWallet(address, BigInteger.ZERO);
    }

    public EthWallet withdrawn(BigInteger amount) {
        return new EthWallet(address, balance.subtract(amount));
    }

    public EthWallet deposited(BigInteger amount) {
        return new EthWallet(address, balance.add(amount));
    }
}