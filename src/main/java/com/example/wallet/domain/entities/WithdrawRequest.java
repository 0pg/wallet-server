package com.example.wallet.domain.entities;

import java.math.BigInteger;

public record WithdrawRequest(
        String walletAddress,
        String destinationAddress,
        BigInteger amount
        ) {
}
