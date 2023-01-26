package com.example.wallet.server.controller;

import com.example.wallet.server.entity.TransactionEvent;
import com.example.wallet.server.port.TransactionEventPort;
import com.example.wallet.server.service.TransactionService;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/event")
    @ResponseBody
    List<TransactionEvent> getTransactionChanges(@NonNull @RequestParam Optional<String> start, @NonNull @RequestParam Optional<String> end, @NonNull @RequestParam Optional<Integer> size) {
        return transactionEventPort.findTransactionEvents(
                start.map(time -> LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME)).orElse(LocalDate.EPOCH.atStartOfDay()),
                end.map(time -> LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME)).orElse(LocalDateTime.of(9999, 12, 31, 23, 59)),
                size.orElse(10));
    }
}
