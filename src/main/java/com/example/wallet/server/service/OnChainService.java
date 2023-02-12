package com.example.wallet.server.service;

import com.example.wallet.server.ports.BlockEventListener;
import io.reactivex.schedulers.Schedulers;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class OnChainService {
    private final Web3j web3j;
    private final List<BlockEventListener> blockEventListeners;

    private final BigInteger gasLimit;

    private final BigInteger gasPrice;

    public OnChainService(Web3j web3j) {
        this.web3j = web3j;
        this.blockEventListeners = new ArrayList<>();
        this.gasLimit = Transfer.GAS_LIMIT;
        this.gasPrice = DefaultGasProvider.GAS_PRICE;
        listenBlockEvent();
    }

    public String createSendTransaction(@NonNull String pKey, @NonNull BigInteger nonce, @NonNull String dstAddress, @NonNull BigInteger amount) {
        try {
            Credentials credentials = Credentials.create(pKey);
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                    nonce,
                    gasPrice,
                    gasLimit,
                    dstAddress,
                    amount
            );
            String encoded = Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials));
            return web3j.ethSendRawTransaction(encoded).send().getTransactionHash();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BigInteger getNonce(String address) {
        try {
            return web3j.ethGetTransactionCount("0x" + address, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addListener(BlockEventListener listener) {
        blockEventListeners.add(listener);
    }

    private void listenBlockEvent() {
        web3j.blockFlowable(false)
                .map(EthBlock::getBlock)
                .doOnNext(block -> blockEventListeners.forEach(listener -> listener.handle(block)))
                .subscribeOn(Schedulers.newThread())
                .retry()
                .subscribe();
    }
}
