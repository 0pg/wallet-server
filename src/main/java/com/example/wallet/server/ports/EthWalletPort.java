package com.example.wallet.server.ports;

import com.example.wallet.server.entities.EthWalletDTO;
import org.springframework.data.repository.CrudRepository;

public interface EthWalletPort extends CrudRepository<EthWalletDTO, String> {
    default EthWalletDTO getEthWallet(String address) {
        return findById(address).orElseThrow(() -> new RuntimeException());
    }
}
