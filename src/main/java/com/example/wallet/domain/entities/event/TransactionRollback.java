package com.example.wallet.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRollback(
        long id,
        LocalDateTime occurredAt,
        String walletId,
        String hash,
        BigDecimal balanceChanges
) implements TransactionEvent {
}
