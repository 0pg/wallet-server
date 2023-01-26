package com.example.wallet.domain.entities;

import lombok.NonNull;

import java.math.BigInteger;

public record EthWallet(@NonNull String address, @NonNull BigInteger balance) {

    public EthWallet(String address) {
        this(address, BigInteger.ZERO);
    }
    public EthWallet withdrawn(BigInteger amount) {
        return new EthWallet(address, balance.subtract(amount));
    }

    public EthWallet deposited(BigInteger amount) {
        return new EthWallet(address, balance.add(amount));
    }
}