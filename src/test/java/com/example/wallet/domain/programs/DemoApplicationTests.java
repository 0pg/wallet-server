package com.example.wallet.domain.programs;

import com.example.wallet.domain.entities.Transaction;
import com.example.wallet.domain.entities.event.TransactionConfirmed;
import com.example.wallet.domain.entities.event.WalletCreated;
import com.example.wallet.server.entities.TransactionDTO;
import com.example.wallet.server.entities.TransactionEvent;
import com.example.wallet.server.mapper.EthWalletMapper;
import com.example.wallet.server.mapper.TransactionEventMapper;
import com.example.wallet.server.mapper.TransactionMapper;
import com.example.wallet.server.ports.TransactionEventPort;
import com.example.wallet.server.ports.TransactionPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigInteger;
import java.time.LocalDateTime;

@ActiveProfiles("test")
@DataJpaTest
class DemoApplicationTests {

    @Autowired
    private TransactionPort transactionPort;

    @Autowired
    private TransactionEventPort transactionEventPort;

    @Test
    void contextLoads() {
        TransactionDTO tx = new TransactionDTO("id", "src", "dst", BigInteger.ONE);
        transactionPort.save(tx);
        TransactionDTO result = transactionPort.findById("id").get();
        Assertions.assertEquals(tx.getId(), result.getId());
        Transaction dTx = TransactionMapper.INSTANCE.toEntity(tx);
        Assertions.assertEquals(dTx.id(), tx.getId());

        TransactionConfirmed confirmed = new TransactionConfirmed(1L, LocalDateTime.MIN, "id", 12);
        TransactionEvent event = TransactionEventMapper.INSTANCE.fromConfirmed(confirmed);
        System.out.println(event.getStatus());

        WalletCreated e = new WalletCreated(1L, "s", "sd", LocalDateTime.MIN);
        System.out.println(EthWalletMapper.INSTANCE.fromCreatedEvent(e).balance().longValue());

    }
}
