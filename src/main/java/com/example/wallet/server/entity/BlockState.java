package com.example.wallet.server.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Entity(name = "block_state")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BlockState {
    @Id
    @Column(name = "hash")
    private String hash;
    private BigInteger number;
}
