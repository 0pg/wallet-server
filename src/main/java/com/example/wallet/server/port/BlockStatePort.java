package com.example.wallet.server.port;

import com.example.wallet.server.entity.BlockState;
import org.springframework.data.repository.CrudRepository;

public interface BlockStatePort extends CrudRepository<BlockState, String> {
}
