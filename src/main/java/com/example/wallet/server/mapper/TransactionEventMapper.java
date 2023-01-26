package com.example.wallet.server.mapper;

import com.example.wallet.domain.entity.event.TransactionCommitted;
import com.example.wallet.domain.entity.event.TransactionConfirmed;
import com.example.wallet.domain.entity.event.TransactionRollback;
import com.example.wallet.server.entity.TransactionEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionEventMapper {
    TransactionEventMapper INSTANCE = Mappers.getMapper(TransactionEventMapper.class);

    @Mapping(target = "confirmationCount", source = "committedCount")
    @Mapping(target = "status", constant = "Mined")
    TransactionEvent fromCommitted(TransactionCommitted event);

    @Mapping(target = "confirmationCount", source = "confirmedCount")
    @Mapping(target = "status", constant = "Confirmed")
    TransactionEvent fromConfirmed(TransactionConfirmed event);

    @Mapping(target = "confirmationCount", constant = "0")
    @Mapping(target = "status", constant = "Failed")
    TransactionEvent fromRollback(TransactionRollback event);
}
