package com.example.wallet.server.controller;

import com.example.wallet.server.entities.TransactionEvent;
import com.example.wallet.server.ports.TransactionEventPort;
import com.example.wallet.server.service.TransactionService;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@Controller
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    private final TransactionEventPort transactionEventPort;

    public TransactionController(TransactionService transactionService, TransactionEventPort transactionEventPort) {
        this.transactionService = transactionService;
        this.transactionEventPort = transactionEventPort;
    }

    @PostMapping
    @ResponseBody
    String create(
            @NonNull String srcAddress,
            @NonNull String password,
            @NonNull String dstAddress,
            @NonNull BigInteger amount
    ) {
        return transactionService.send(password, srcAddress, dstAddress, amount);
    }

    @GetMapping("/event/{transactionId}")
    @ResponseBody
    List<TransactionEvent> getTransactionChanges(@NonNull @PathVariable("transactionId") String transactionId) {
        return transactionEventPort.findAllByTransactionId(transactionId);
    }
}
