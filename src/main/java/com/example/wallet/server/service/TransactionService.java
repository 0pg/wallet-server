package com.example.wallet.server.service;

import com.example.wallet.domain.entity.Transaction;
import com.example.wallet.domain.entity.event.DomainEvent;
import com.example.wallet.domain.program.Result;
import com.example.wallet.domain.program.TransactionProgram;
import com.example.wallet.server.adaptor.DomainEventLogger;
import com.example.wallet.server.adaptor.PersistEventHandler;
import com.example.wallet.server.exception.InvalidInput;
import com.example.wallet.server.mapper.TransactionMapper;
import com.example.wallet.server.port.EthWalletPort;
import com.example.wallet.server.port.TransactionPort;
import com.example.wallet.server.util.AESCipherUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigInteger;
import java.util.List;
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
            throw new InvalidInput("create transaction", e);
        }
    }

    @Override
    public void handle(@NonNull EthBlock.Block block) {
        try {
            handleBlockTransaction(block);
            handleExternalBlockTransaction(block);
        } catch (Exception e) {
            log.error("new block handler", e);
            throw e;
        }

    }

    private void handleBlockTransaction(EthBlock.Block block) {
        Stream.concat(
                getBlockTransactionStream(block)
                        .map(tx -> transactionPort.findTransactionEntity(tx.getHash()))
                        .filter(Optional::isPresent)
                        .map(Optional::get),
                transactionPort.findMinedTransactions().stream()
                        .map(TransactionMapper.INSTANCE::toEntity)
        ).forEach(tx -> commit(tx, 1));
    }

    private void handleExternalBlockTransaction(EthBlock.Block block) {
        getBlockTransactionStream(block)
                .filter(tx ->
                        Optional.ofNullable(tx.getTo())
                                .flatMap(walletPort::findById)
                                .isPresent()
                )
                .forEach(tx -> {
                    Result<Transaction> created = transactionProgram.createTransaction(tx.getHash(), tx.getFrom(), tx.getTo(), tx.getValue());
                    Result<Transaction> committed = transactionProgram.commit(created.value, 1);
                    List<DomainEvent> events = Stream.concat(created.events.stream(), committed.events.stream()).toList();
                    persistEventHandler.persistEvents(events);
                    eventLogger.logEvents(events);
                });
    }

    private Stream<org.web3j.protocol.core.methods.response.Transaction> getBlockTransactionStream(EthBlock.Block block) {
        return block.getTransactions().stream()
                .map(result -> ((EthBlock.TransactionObject) result.get()));
    }

    private void commit(Transaction tx, int count) {
        try {
            Result<Transaction> result = transactionProgram.commit(tx, count);
            persistEventHandler.persistEvents(result.events);
            eventLogger.logEvents(result.events);
        } catch (Exception e) {
            log.error("commit transaction", e);
            throw e;
        }
    }
}
