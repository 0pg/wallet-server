package com.example.wallet.server.entity;

import com.example.wallet.domain.entity.TransactionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "transaction_events", indexes = @Index(name = "idx_occurred_at", columnList = "occurredAt"))
public class TransactionEvent {
    @Id
    @GeneratedValue
    private long eventId;
    private String transactionId;
    private int confirmationCount;
    private LocalDateTime occurredAt;
    private TransactionStatus status;
}
