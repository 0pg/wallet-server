package com.example.wallet.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "eth_wallet")
public final class EthWalletDTO {
    @Id
    @Column(name = "address")
    private String address;
    private BigInteger balance;
    private String secret;

    public EthWalletDTO(String address, String secret) {
        this(address, BigInteger.ZERO, secret);
    }

    public EthWalletDTO withdrawn(BigInteger amount) {
        return new EthWalletDTO(address, balance.subtract(amount), secret);
    }

    public EthWalletDTO deposited(BigInteger amount) {
        return new EthWalletDTO(address, balance.add(amount), secret);
    }
}