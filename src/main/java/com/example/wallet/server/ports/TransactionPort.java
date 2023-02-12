package com.example.wallet.server.ports;

import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.server.entities.TransactionDTO;
import com.example.wallet.server.mapper.TransactionMapper;
import lombok.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionPort extends CrudRepository<TransactionDTO, String> {
    default Optional<Transaction> findTransactionEntity(@NonNull String id) {
        return findById(id).map(TransactionMapper.INSTANCE::toEntity);
    }

    @Query(value = "SELECT t FROM TransactionDTO t WHERE t.status = Mined")
    List<TransactionDTO> findMinedTransactions();
}
