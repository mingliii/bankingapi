package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.domain.TransactionResource;
import com.demo.bankingapi.entity.Transaction;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

// todo test

@Component
public class TransactionToTransactionResourceConverter implements Converter<Transaction, TransactionResource> {

    @Override
    public TransactionResource convert(Transaction transaction) {
        return TransactionResource.builder()
                .accountNumber(transaction.getAccount().getAccountNumber())
                .createdAt(transaction.getCreatedAt())
                .type(transaction.getType() != null ? transaction.getType().name() : null)
                .inAmount(transaction.getInAmount())
                .outAmount(transaction.getOutAmount())
                .balance(transaction.getBalance())
                .currency(transaction.getCurrency() != null ? transaction.getCurrency().name() : null)
                .description(transaction.getDescription())
                .build();
    }
}
