package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.resource.TransactionResource;
import com.demo.bankingapi.entity.Currency;
import com.demo.bankingapi.entity.Transaction;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

// todo test

@Component
public class TransactionResourceToTransactionConverter implements Converter<TransactionResource, Transaction> {

    @Override
    public Transaction convert(TransactionResource transactionResource) {
        return Transaction.builder()
                .createdAt(transactionResource.getCreatedAt())
                .type(Transaction.Type.from(transactionResource.getType()))
                .inAmount(transactionResource.getInAmount())
                .outAmount(transactionResource.getOutAmount())
                .balance(transactionResource.getBalance())
                .currency(Currency.from(transactionResource.getCurrency()))
                .description(transactionResource.getDescription())
                .build();
    }
}
