package com.example.wallet.server.ports;

import org.web3j.protocol.core.methods.response.EthBlock;

public interface BlockEventListener {
    void handle(EthBlock.Block block);
}
