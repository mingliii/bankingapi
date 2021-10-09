package com.demo.bankingapi.service;

import com.demo.bankingapi.domain.TransactionResource;
import com.demo.bankingapi.entity.Account;
import com.demo.bankingapi.entity.Currency;
import com.demo.bankingapi.entity.Transaction;
import com.demo.bankingapi.repository.TransactionRepository;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.demo.bankingapi.entity.Transaction.Type.TRANSFER;

// todo test

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final ConversionService conversionService;

    public TransactionService(TransactionRepository transactionRepository, ConversionService conversionService) {
        this.transactionRepository = transactionRepository;
        this.conversionService = conversionService;
    }

    @Transactional(readOnly = true)
    public List<TransactionResource> getTransactions(Long accountNumber, Pageable pageable) {
        return transactionRepository.findAllByAccount_AccountNumber(accountNumber, pageable)
                .stream().map(transaction -> conversionService.convert(transaction, TransactionResource.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void createTransactions(Account from, Account to, BigDecimal amount, Currency currency) {

        Transaction fromTransaction = Transaction.builder()
                .account(from)
                .balance(from.getBalance())
                .outAmount(amount)
                .currency(currency)
                .description(String.format("A transfer(%s %s) made to Account(%s)", currency.getSymbol(), amount, to.getAccountNumber()))
                .type(TRANSFER)
                .build();

        Transaction toTransaction = Transaction.builder()
                .account(to)
                .balance(to.getBalance())
                .inAmount(amount)
                .currency(currency)
                .description(String.format("A transfer(%s %s) received from Account(%s)", currency.getSymbol(), amount, from.getAccountNumber()))
                .type(TRANSFER)
                .build();

        transactionRepository.save(fromTransaction);
        transactionRepository.save(toTransaction);
    }
}
