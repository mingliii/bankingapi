package com.demo.bankingapi.service;

import com.demo.bankingapi.entity.Account;
import com.demo.bankingapi.entity.Currency;
import com.demo.bankingapi.entity.Transaction;
import com.demo.bankingapi.repository.TransactionRepository;
import com.demo.bankingapi.resource.TransactionResource;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ConversionService conversionService;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void getTransactions() {

        Long accountNumber = 1L;
        Transaction.Type type = Transaction.Type.TRANSFER;
        LocalDateTime from = LocalDateTime.MIN;
        LocalDateTime to = LocalDateTime.MAX;
        Pageable pageable = PageRequest.of(5, 10, Sort.Direction.ASC, "createdAt");

        // given
        List<Transaction> transactions = generator.objects(Transaction.class, 5).collect(Collectors.toUnmodifiableList());

        when(transactionRepository.findAllByAccount_AccountNumberAndTypeAndCreatedAtBetween(
                accountNumber, type, from, to, pageable))
                .thenReturn(new PageImpl<>(transactions));

        // when
        transactionService.getTransactions(accountNumber, type, from, to, pageable);

        // then
        verify(conversionService, times(5)).convert(any(Transaction.class), eq(TransactionResource.class));
    }

    @Test
    void createDepositTransactions() {
        // given
        Account account = generator.nextObject(Account.class);
        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);

        // when
        transactionService.createDepositTransactions(account, new BigDecimal("100.00"), Currency.GBP);

        // then
        verify(transactionRepository).save(transactionArgumentCaptor.capture());
        Transaction transaction = transactionArgumentCaptor.getValue();
        assertEquals(new BigDecimal("100.00"), transaction.getBalance());
        assertEquals(Currency.GBP, transaction.getCurrency());
        assertEquals(account, transaction.getAccount());
    }

    @Test
    void createTransferTransactions() {
        // given
        Account from = generator.nextObject(Account.class);
        from.setBalance(new BigDecimal("3.00"));

        Account to = generator.nextObject(Account.class);
        to.setBalance(new BigDecimal("30.00"));

        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);

        // when
        transactionService.createTransferTransactions(from, to, new BigDecimal("100.00"), Currency.GBP);

        // then
        verify(transactionRepository, times(2)).save(transactionArgumentCaptor.capture());
        List<Transaction> transactions = transactionArgumentCaptor.getAllValues();

        assertEquals(new BigDecimal("3.00"), transactions.get(0).getBalance());
        assertEquals(Currency.GBP, transactions.get(0).getCurrency());
        assertEquals(from, transactions.get(0).getAccount());
        assertEquals(BigDecimal.ZERO, transactions.get(0).getInAmount());
        assertEquals(new BigDecimal("100.00"), transactions.get(0).getOutAmount());

        assertEquals(new BigDecimal("30.00"), transactions.get(1).getBalance());
        assertEquals(Currency.GBP, transactions.get(1).getCurrency());
        assertEquals(to, transactions.get(1).getAccount());
        assertEquals(new BigDecimal("100.00"), transactions.get(1).getInAmount());
        assertEquals(BigDecimal.ZERO, transactions.get(1).getOutAmount());
    }
}