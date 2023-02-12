package com.example.wallet.server.mapper;

import com.example.wallet.domain.entities.EthWallet;
import com.example.wallet.server.entities.EthWalletDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EthWalletMapper {
    EthWalletMapper INSTANCE = Mappers.getMapper(EthWalletMapper.class);

    EthWallet toEntity(EthWalletDTO dto);

    EthWalletDTO toDTO(EthWallet entity);
}
