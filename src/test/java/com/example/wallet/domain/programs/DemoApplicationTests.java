package com.example.wallet.domain.programs;

import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.server.entities.TransactionDTO;
import com.example.wallet.server.mapper.TransactionMapper;
import com.example.wallet.server.ports.TransactionPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigInteger;

@ActiveProfiles("test")
@DataJpaTest
class DemoApplicationTests {

    @Autowired
    private TransactionPort transactionPort;

    @Test
    void contextLoads() {
        TransactionDTO tx = new TransactionDTO("id", "src", "dst", BigInteger.ONE);
        transactionPort.save(tx);
        TransactionDTO result = transactionPort.findById("id").get();
        Assertions.assertEquals(tx.getId(), result.getId());
        Transaction dTx = TransactionMapper.INSTANCE.toEntity(tx);
        Assertions.assertEquals(dTx.id(), tx.getId());
    }
}
