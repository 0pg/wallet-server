package com.example.wallet.server.mapper;

import com.example.wallet.domain.entities.event.TransactionCommitted;
import com.example.wallet.domain.entities.event.TransactionConfirmed;
import com.example.wallet.server.entities.TransactionEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionEventMapper {
    TransactionEventMapper INSTANCE = Mappers.getMapper(TransactionEventMapper.class);

    @Mapping(target = "confirmationCount", source = "count")
    TransactionEvent fromCommitted(TransactionCommitted event);

    @Mapping(target = "confirmationCount", constant = "0")
    TransactionEvent fromConfirmed(TransactionConfirmed event);
}
