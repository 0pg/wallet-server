package com.example.wallet.server.mapper;

import com.example.wallet.domain.entity.Transaction;
import com.example.wallet.domain.entity.event.TransactionStarted;
import com.example.wallet.server.entity.TransactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    Transaction toEntity(TransactionDTO dto);

    TransactionDTO toDTO(Transaction entity);

    Transaction fromStartedEvent(TransactionStarted event);
}
