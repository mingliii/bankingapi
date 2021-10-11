package com.demo.bankingapi.service;

import com.demo.bankingapi.resource.TransactionResource;
import com.demo.bankingapi.entity.Account;
import com.demo.bankingapi.entity.Currency;
import com.demo.bankingapi.entity.Transaction;
import com.demo.bankingapi.repository.TransactionRepository;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.demo.bankingapi.entity.Transaction.Type.DEPOSIT;
import static com.demo.bankingapi.entity.Transaction.Type.TRANSFER;
import static java.lang.String.format;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final ConversionService conversionService;

    public TransactionService(TransactionRepository transactionRepository, ConversionService conversionService) {
        this.transactionRepository = transactionRepository;
        this.conversionService = conversionService;
    }

    @Transactional(readOnly = true)
    public List<TransactionResource> getTransactions(Long accountNumber, Transaction.Type type, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return transactionRepository.findAllByAccount_AccountNumberAndTypeAndCreatedAtBetween(accountNumber, type, from, to, pageable)
                .stream().map(transaction -> conversionService.convert(transaction, TransactionResource.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void createDepositTransactions(Account account, BigDecimal amount, Currency currency) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .balance(amount)
                .inAmount(amount)
                .currency(currency)
                .description(format("A deposit(%s%s)", currency.getSymbol(), amount))
                .type(DEPOSIT)
                .build();

        transactionRepository.save(transaction);
    }

    @Transactional
    public void createTransferTransactions(Account from, Account to, BigDecimal amount, Currency currency) {

        Transaction fromTransaction = Transaction.builder()
                .account(from)
                .balance(from.getBalance())
                .inAmount(BigDecimal.ZERO)
                .outAmount(amount)
                .currency(currency)
                .description(format("A transfer(%s%s) made to Account(%s)", currency.getSymbol(), amount, to.getAccountNumber()))
                .type(TRANSFER)
                .build();

        Transaction toTransaction = Transaction.builder()
                .account(to)
                .balance(to.getBalance())
                .inAmount(amount)
                .outAmount(BigDecimal.ZERO)
                .currency(currency)
                .description(format("A transfer(%s%s) received from Account(%s)", currency.getSymbol(), amount, from.getAccountNumber()))
                .type(TRANSFER)
                .build();

        transactionRepository.save(fromTransaction);
        transactionRepository.save(toTransaction);
    }
}
