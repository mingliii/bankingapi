package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.domain.TransactionResource;
import com.demo.bankingapi.entity.Transaction;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TransactionToTransactionResourceConverter implements Converter<Transaction, TransactionResource> {

    @Override
    public TransactionResource convert(Transaction transaction) {
        return TransactionResource.builder()
                .accountNumber(transaction.getAccount().getAccountNumber())
                .createdAt(transaction.getCreatedAt())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .balance(transaction.getBalance())
                .description(transaction.getDescription())
                .build();
    }
}
