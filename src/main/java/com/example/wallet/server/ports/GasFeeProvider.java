package com.example.wallet.server.ports;

import java.math.BigInteger;

public interface GasFeeProvider {
    BigInteger gasLimit();
    BigInteger baseFee();
    BigInteger premiumFee();
}
