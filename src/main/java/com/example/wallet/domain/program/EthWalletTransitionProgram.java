package com.example.wallet.domain.program;

import com.example.wallet.domain.DomainEventHandler;
import com.example.wallet.domain.entity.EthWallet;
import com.example.wallet.domain.entity.event.*;

import java.util.function.Function;

public class EthWalletTransitionProgram implements DomainEventHandler<Function<EthWallet, EthWallet>> {
    @Override
    public Function<EthWallet, EthWallet> handle(TransactionConfirmed event) {
       return Function.identity();
    }

    @Override
    public Function<EthWallet, EthWallet> handle(TransactionCommitted event) {
       return Function.identity();
    }

    @Override
    public Function<EthWallet, EthWallet> handle(TransactionRollback event) {
       return Function.identity();
    }

    @Override
    public Function<EthWallet, EthWallet> handle(TransactionStarted event) {
       return Function.identity();
    }

    @Override
    public Function<EthWallet, EthWallet> handle(Deposited event) {
        return (EthWallet ethWallet) -> ethWallet.deposited(event.amount());
    }

    @Override
    public Function<EthWallet, EthWallet> handle(Withdrawn event) {
        return (EthWallet ethWallet) -> ethWallet.withdrawn(event.amount());
    }

    @Override
    public Function<EthWallet, EthWallet> handle(WalletCreated event) {
        return Function.identity();
    }
}
