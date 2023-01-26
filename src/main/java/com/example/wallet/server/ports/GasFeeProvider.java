package com.example.wallet.server.adaptor;

import java.math.BigInteger;

public interface GasFeeProvider {
    BigInteger gasLimit();
    BigInteger baseFee();
    BigInteger premiumFee();
}
