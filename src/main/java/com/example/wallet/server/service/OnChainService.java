package com.example.wallet.server.service;

import com.example.wallet.server.ports.GasFeeProvider;
import com.example.wallet.server.ports.TransactionPort;
import lombok.NonNull;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;

public class OnChainService {
    private final Web3j web3j;
    private final GasFeeProvider gasFeeProvider;

    public OnChainService(Web3j web3j, GasFeeProvider gasFeeProvider) {
        this.web3j = web3j;
        this.gasFeeProvider = gasFeeProvider;
    }

    public String createSendTransaction(@NonNull String pKey, @NonNull BigInteger nonce, @NonNull String dstAddress, @NonNull BigInteger amount) {
        try {
            Credentials credentials = Credentials.create(pKey);
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                    nonce,
                    gasFeeProvider.gasLimit(),
                    dstAddress,
                    amount,
                    gasFeeProvider.premiumFee(),
                    gasFeeProvider.baseFee()
            );
            String encoded = Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials));
            return web3j.ethSendRawTransaction(encoded).send().getTransactionHash();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BigInteger getNonce(String address) {
        try {
            return web3j.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
