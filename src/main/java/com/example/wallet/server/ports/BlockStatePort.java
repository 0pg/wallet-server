package com.example.wallet.server.ports;

import com.example.wallet.server.entities.BlockState;
import org.springframework.data.repository.CrudRepository;

public interface BlockStatePort extends CrudRepository<BlockState, String> {
}
