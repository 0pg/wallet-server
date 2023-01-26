package com.example.wallet.server.port;

import com.example.wallet.server.entity.EthWalletDTO;
import com.example.wallet.server.exception.DataNotFound;
import org.springframework.data.repository.CrudRepository;

public interface EthWalletPort extends CrudRepository<EthWalletDTO, String> {
    default EthWalletDTO getEthWallet(String address) {
        return findById(address).orElseThrow(() -> new DataNotFound("EthWallet", address));
    }
}
