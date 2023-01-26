package com.example.wallet.server.port;

import com.example.wallet.server.entity.TransactionEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionEventPort extends CrudRepository<TransactionEvent, Long> {
    @Query(value = "SELECT t FROM TransactionEvent t WHERE t.transactionId = ?1")
    List<TransactionEvent> findAllByTransactionId(String transactionId);

    @Query(value = "SELECT * FROM transaction_events WHERE occurred_at >= :start and occurred_at <= :end LIMIT :size", nativeQuery = true)
    List<TransactionEvent> findTransactionEvents(LocalDateTime start, LocalDateTime end, int size);
}
