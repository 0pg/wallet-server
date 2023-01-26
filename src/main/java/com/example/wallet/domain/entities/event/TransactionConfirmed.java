package com.example.wallet.domain.event;

import java.time.LocalDateTime;

public record TransactionConfirmed(
        long id,
        LocalDateTime occurredAt,
        String walletId,
        String hash
) implements TransactionEvent {
}
