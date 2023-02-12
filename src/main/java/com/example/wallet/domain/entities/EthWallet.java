package com.example.wallet.domain.entities;

import java.math.BigInteger;

public record EthWallet(String address, String secret, BigInteger balance) {
    public static EthWallet create(String address, String secret) {
        return new EthWallet(address, secret, BigInteger.ZERO);
    }

    public EthWallet withdrawn(BigInteger amount) {
        return new EthWallet(address, secret, balance.subtract(amount));
    }

    public EthWallet deposited(BigInteger amount) {
        return new EthWallet(address, secret, balance.add(amount));
    }
}