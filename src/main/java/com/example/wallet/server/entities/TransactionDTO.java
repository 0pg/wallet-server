package com.example.wallet.server.entities;

import com.example.wallet.domain.entities.TransactionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public final class TransactionDTO {
    @Id
    @Column(name = "id")
    private String id;
    private String srcAddress;
    private String dstAddress;
    private int confirmationCount;
    private BigInteger amount;
    private TransactionStatus status;

    public TransactionDTO(String id, String srcAddress, String dstAddress, BigInteger amount) {
        this(id, srcAddress, dstAddress, 0, amount, TransactionStatus.Pending);
    }

    public TransactionDTO statusUpdated(TransactionStatus updatedStatus) {
        return new TransactionDTO(id, srcAddress, dstAddress, confirmationCount, amount, updatedStatus);
    }

    public TransactionDTO confirmed(int count) {
        return new TransactionDTO(id, srcAddress, dstAddress, confirmationCount + count, amount, status);
    }
}
