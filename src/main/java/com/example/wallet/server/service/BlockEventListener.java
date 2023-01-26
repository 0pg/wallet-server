package com.example.wallet.server.service;

import org.web3j.protocol.core.methods.response.EthBlock;

public interface BlockEventListener {
    void handle(EthBlock.Block block);
}
