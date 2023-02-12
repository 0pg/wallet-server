package com.example.wallet.server.ports;

import com.example.wallet.server.entities.EthWalletDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EthWalletPort extends CrudRepository<EthWalletDTO, String> {
}
