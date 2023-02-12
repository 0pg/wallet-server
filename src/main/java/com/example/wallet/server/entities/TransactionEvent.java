package com.example.wallet.server.entities;

import com.example.wallet.domain.entities.TransactionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "transaction_events")
public class TransactionEvent {
    @Id
    @GeneratedValue
    private long eventId;
    private String transactionId;
    private int confirmationCount;
    private LocalDateTime occurredAt;
    private TransactionStatus status;
}
