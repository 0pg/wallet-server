package com.example.wallet.domain.entities.event;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record Withdrawn(
        String walletAddress,
        BigInteger amount,
        LocalDateTime occurredAt
) implements DomainEvent {
}
