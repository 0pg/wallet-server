package com.example.wallet.server.ports;

import com.example.wallet.server.entities.TransactionDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionPort extends CrudRepository<TransactionDTO, String> {
}
