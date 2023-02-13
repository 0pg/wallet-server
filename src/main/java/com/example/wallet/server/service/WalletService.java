package com.example.wallet.server.service;

import com.example.wallet.domain.entities.EthWallet;
import com.example.wallet.domain.entities.event.DomainEvent;
import com.example.wallet.domain.programs.EthWalletProgram;
import com.example.wallet.domain.programs.Result;
import com.example.wallet.server.adaptor.DomainEventLogger;
import com.example.wallet.server.adaptor.PersistEventHandler;
import com.example.wallet.server.exception.InvalidInput;
import com.example.wallet.server.utils.AESCipherUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import java.util.List;

@Service
public class WalletService {

    private final EthWalletProgram walletProgram;
    private final PersistEventHandler persistEventHandler;
    private final DomainEventLogger eventLogger;

    public WalletService(EthWalletProgram walletProgram, PersistEventHandler persistEventHandler, DomainEventLogger eventLogger) {
        this.walletProgram = walletProgram;
        this.persistEventHandler = persistEventHandler;
        this.eventLogger = eventLogger;
    }

    public String generateWallet(String password) {
        try {
            ECKeyPair keyPair = Keys.createEcKeyPair();
            WalletFile walletFile = Wallet.createLight(password, keyPair);
            String secret = AESCipherUtil.generateSecret(password, walletFile);
            Result<EthWallet> result = walletProgram.createWallet("0x" + walletFile.getAddress(), secret);
            persist(result.events);
            logEvents(result.events);
            return result.value.address();
        } catch (Exception e) {
            throw new InvalidInput("create wallet", e);
        }
    }

    @Transactional
    private void persist(List<DomainEvent> events) {
        events.forEach(event -> event.accept(persistEventHandler));
    }

    private void logEvents(List<DomainEvent> events) {
        events.forEach(event -> event.accept(eventLogger));
    }
}
