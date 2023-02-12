package com.example.wallet.server.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String address;
    private BigInteger balance;

    public EthWalletDTO(String address) {
        this(address, BigInteger.ZERO);
    }

    public EthWalletDTO withdrawn(BigInteger amount) {
        return new EthWalletDTO(address, balance.subtract(amount));
    }

    public EthWalletDTO deposited(BigInteger amount) {
        return new EthWalletDTO(address, balance.add(amount));
    }
}