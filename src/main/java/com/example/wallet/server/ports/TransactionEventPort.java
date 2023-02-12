package com.example.wallet.server.ports;

import com.example.wallet.server.entities.TransactionEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionEventPort extends CrudRepository<TransactionEvent, Long> {
    @Query(value = "SELECT t FROM TransactionEvent t WHERE t.transactionId = ?1")
    List<TransactionEvent> findAllByTransactionId(String transactionId);
}
