package com.example.wallet.server.config;

import com.example.wallet.domain.programs.EthWalletProgram;
import com.example.wallet.domain.programs.EthWalletTransitionProgram;
import com.example.wallet.domain.programs.TransactionProgram;
import com.example.wallet.domain.programs.TxTransitionProgram;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Callable;

@Configuration
public class ServerConfig {

    @Bean
    public Callable<LocalDateTime> timestampProvider() {
        return LocalDateTime::now;
    }

    @Bean
    Callable<Long> eventIdProvider() {
        return () -> UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    @Bean
    public EthWalletProgram walletProgram(Callable<LocalDateTime> timestampProvider, Callable<Long> eventIdProvider) {
        return new EthWalletProgram(timestampProvider, eventIdProvider);
    }

    @Bean
    public TransactionProgram transactionProgram(Callable<LocalDateTime> timestampProvider, Callable<Long> eventIdProvider) {
        return new TransactionProgram(timestampProvider, eventIdProvider);
    }

    @Bean
    public EthWalletTransitionProgram walletTransitionProgram() {
        return new EthWalletTransitionProgram();
    }

    @Bean
    public TxTransitionProgram txTransitionProgram() {
        return new TxTransitionProgram();
    }

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService("https://tn.henesis.io/ethereum/goerli?clientId=815fcd01324b8f75818a755a72557750"));
    }
}
