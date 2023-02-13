package com.example.wallet.server.service;

import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.domain.programs.Result;
import com.example.wallet.domain.programs.TransactionProgram;
import com.example.wallet.server.adaptor.DomainEventLogger;
import com.example.wallet.server.adaptor.PersistEventHandler;
import com.example.wallet.server.mapper.TransactionMapper;
import com.example.wallet.server.ports.BlockEventListener;
import com.example.wallet.server.ports.EthWalletPort;
import com.example.wallet.server.ports.TransactionPort;
import com.example.wallet.server.utils.AESCipherUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class TransactionService implements BlockEventListener {
    private final TransactionProgram transactionProgram;
    private final TransactionPort transactionPort;
    private final PersistEventHandler persistEventHandler;
    private final OnChainService onChainService;
    private final EthWalletPort walletPort;
    private final DomainEventLogger eventLogger;

    public TransactionService(@NonNull TransactionProgram transactionProgram, @NonNull TransactionPort transactionPort, @NonNull PersistEventHandler persistEventHandler, @NonNull OnChainService onChainService, @NonNull EthWalletPort walletPort, @NonNull DomainEventLogger eventLogger) {
        this.transactionProgram = transactionProgram;
        this.transactionPort = transactionPort;
        this.persistEventHandler = persistEventHandler;
        this.onChainService = onChainService;
        this.walletPort = walletPort;
        this.eventLogger = eventLogger;

        onChainService.addListener(this);
    }

    public String send(@NonNull String password, @NonNull String srcAddress, @NonNull String dstAddress, @NonNull BigInteger amount) {
        try {
            String pKey = AESCipherUtil.extractPKey(password, walletPort.getEthWallet(srcAddress).getSecret());
            BigInteger nonce = onChainService.getNonce(srcAddress);
            String transactionHash = onChainService.createSendTransaction(pKey, nonce, dstAddress, amount);
            Result<Transaction> result = transactionProgram.createTransaction(transactionHash, srcAddress, dstAddress, amount);
            persistEventHandler.persistEvents(result.events);
            eventLogger.logEvents(result.events);
            return result.value.id();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(@NonNull EthBlock.Block block) {
        try {
            Stream.concat(
                    block.getTransactions().stream()
                            .map(result -> ((EthBlock.TransactionHash) result).get())
                            .map(transactionPort::findTransactionEntity)
                            .filter(Optional::isPresent)
                            .map(Optional::get),
                    transactionPort.findMinedTransactions().stream()
                            .map(TransactionMapper.INSTANCE::toEntity)
            ).forEach(tx -> commit(tx, 1));
        } catch (Exception e) {
            log.error(e.getCause().getMessage());
        }
    }

    private void commit(Transaction tx, int count) {
        try {
            Result<Transaction> result = transactionProgram.commit(tx, count);
            persistEventHandler.persistEvents(result.events);
            eventLogger.logEvents(result.events);
        } catch (Exception e) {
            log.error(e.getCause().getMessage());
        }
    }
}
