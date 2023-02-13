package com.example.wallet.server.service;

import com.example.wallet.server.entities.BlockState;
import com.example.wallet.server.exception.IOException;
import com.example.wallet.server.ports.BlockStatePort;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class OnChainService {
    private final Web3j web3j;
    private final List<BlockEventListener> blockEventListeners;
    private final BlockStatePort blockStatePort;

    private final BigInteger gasLimit;

    private BigInteger gasPrice;

    public OnChainService(Web3j web3j, BlockStatePort blockStatePort) {
        this.web3j = web3j;
        this.blockStatePort = blockStatePort;
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
            throw new IOException("send transaction", e);
        }
    }

    public BigInteger getNonce(String address) {
        try {
            return web3j.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        } catch (Exception e) {
            throw new IOException("get nonce", e);
        }
    }

    private void updateGasPrice(EthBlock.Block block) {
        this.gasPrice = (block.getGasLimit().subtract(block.getGasUsed()))
                .divide(block.getGasLimit())
                .multiply(DefaultGasProvider.GAS_PRICE);
    }

    public void addListener(BlockEventListener listener) {
        blockEventListeners.add(listener);
    }

    private void listenBlockEvent() {
        web3j.blockFlowable(true).map(EthBlock::getBlock)
                .doOnNext(block -> blockEventListeners.forEach(listener -> listener.handle(block)))
                .doOnNext(this::insertBlockState)
                .doOnNext(this::updateGasPrice)
                .subscribeOn(Schedulers.newThread())
                .doOnError(e -> log.error("block listener", e))
                .retry()
                .subscribe();
    }

    private void syncPendingBlocks() {
        try {
            BigInteger current = StreamSupport.stream(blockStatePort.findAll().spliterator(), false)
                    .map(BlockState::getNumber)
                    .max(BigInteger::compareTo)
                    .orElse(BigInteger.valueOf(-1));
            EthBlock.Block latest = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true).send().getBlock();
            BigInteger latestNum = latest.getNumber();
            Flowable<EthBlock.Block> blocks;
            if (current.compareTo(latestNum) < 0) {
                blocks = LongStream.range(current.longValue() + 1, latestNum.longValue()).boxed()
                        .map(num ->
                                web3j.ethGetBlockByNumber(
                                                DefaultBlockParameter.valueOf(BigInteger.valueOf(num)),
                                                true)
                                        .flowable()
                                        .map(EthBlock::getBlock)
                                        .doOnError(e -> log.error(e.getMessage()))
                        )
                        .reduce(Flowable.fromArray(latest), Flowable::merge);
            } else {
                blocks = Flowable.fromArray(latest);
            }
            blocks.doOnNext(block -> blockEventListeners.forEach(listener -> listener.handle(block)))
                    .doOnNext(this::insertBlockState)
                    .doOnError(e -> log.error(e.getMessage()))
                    .subscribe();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IOException("sync pending blocks", e);
        }
    }

    private void insertBlockState(EthBlock.Block block) {
        try {
            blockStatePort.save(new BlockState(block.getHash(), block.getNumber()));
        } catch (Exception e) {
            log.error("insert block state", e);
            throw e;
        }
    }
}