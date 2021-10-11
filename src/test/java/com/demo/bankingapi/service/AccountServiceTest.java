package com.demo.bankingapi.service;

import com.demo.bankingapi.entity.Account;
import com.demo.bankingapi.entity.Currency;
import com.demo.bankingapi.entity.Customer;
import com.demo.bankingapi.repository.AccountRepository;
import com.demo.bankingapi.repository.CustomerRepository;
import com.demo.bankingapi.resource.AccountResource;
import com.demo.bankingapi.resource.TransactionResource;
import com.demo.bankingapi.resource.TransferResource;
import com.demo.bankingapi.service.exception.InsufficientBalanceException;
import com.demo.bankingapi.service.exception.NotFoundException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private ConversionService conversionService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void getAllAccounts() {
        // given
        int expected = 5;
        List<Account> accounts = generator.objects(Account.class, 5).collect(Collectors.toList());

        when(accountRepository.findAll()).thenReturn(accounts);

        // when
        List<AccountResource> accountResources = accountService.getAllAccounts();

        // then
        assertEquals(expected, accountResources.size());
        verify(conversionService, times(expected)).convert(any(Account.class), eq(AccountResource.class));
    }

    @Test
    void getByAccountNumber() {
        // given
        Long accountNumber = 123L;
        Account account = generator.nextObject(Account.class);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        // when
        accountService.getByAccountNumber(accountNumber);

        // then
        verify(conversionService).convert(eq(account), eq(AccountResource.class));
    }

    @Test
    void getByAccountNumberThrowNotFoundException() {
        // given
        Long accountNumber = 123L;
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            // when
            accountService.getByAccountNumber(accountNumber);

            // then
            fail("Should throw exception");
        });
    }

    @Test
    void createAccount() {
        // given
        Long customerNumber = 123L;

        Customer customer = generator.nextObject(Customer.class);
        customer.setCustomerNumber(customerNumber);
        customer.setAccounts(new ArrayList<>());

        AccountResource accountResource = mock(AccountResource.class);
        when(accountResource.getCustomerNumber()).thenReturn(customerNumber);

        when(customerRepository.findByCustomerNumber(eq(customerNumber))).thenReturn(Optional.of(customer));

        Account account = generator.nextObject(Account.class);
        when(conversionService.convert(accountResource, Account.class)).thenReturn(account);

        // when
        accountService.createAccount(accountResource);

        // then
        verify(conversionService).convert(eq(accountResource), eq(Account.class));
        verify(accountRepository).save(eq(account));
        verify(conversionService).convert(eq(account), eq(AccountResource.class));
        assertTrue(customer.getAccounts().contains(account));
        assertEquals(customer, account.getCustomer());
    }

    @Test
    void createAccountWithNoCustomerId() {
        // given
        AccountResource accountResource = mock(AccountResource.class);
        when(accountResource.getCustomerNumber()).thenReturn(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {

            // when
            accountService.createAccount(accountResource);

            fail("Should throw exception");
        });

        //then
        verifyNoInteractions(customerRepository, conversionService, accountRepository);
    }

    @Test
    void createAccountWithCustomerNotExisting() {
        // given
        Long customerNumber = 123L;

        Customer customer = generator.nextObject(Customer.class);
        customer.setCustomerNumber(customerNumber);
        customer.setAccounts(new ArrayList<>());

        AccountResource accountResource = mock(AccountResource.class);
        when(accountResource.getCustomerNumber()).thenReturn(customerNumber);

        when(customerRepository.findByCustomerNumber(eq(customerNumber))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            // when
            accountService.createAccount(accountResource);
        });

        //then
        verify(customerRepository).findByCustomerNumber(eq(customerNumber));
        verifyNoInteractions(conversionService, accountRepository);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void updateAccount() {
        // given
        AccountResource accountResource = generator.nextObject(AccountResource.class);
        Account account = mock(Account.class);
        Account savedAccount = mock(Account.class);

        when(conversionService.convert(accountResource, Account.class)).thenReturn(account);
        when(accountRepository.save(eq(account))).thenReturn(savedAccount);

        // when
        accountService.updateAccount(accountResource);

        // then
        verify(conversionService).convert(eq(accountResource), eq(Account.class));
        verify(conversionService).convert(eq(savedAccount), eq(AccountResource.class));
    }

    @Test
    void transfer() {
        // given
        TransferResource transferResource = TransferResource.builder()
                .fromAccountNumber(123L)
                .toAccountNumber(456L)
                .amount(new BigDecimal("15.99"))
                .build();

        Account from = Account.builder()
                .accountNumber(123L)
                .balance(new BigDecimal("20.00"))
                .build();

        Account to = Account.builder()
                .accountNumber(456L)
                .balance(new BigDecimal("50.00"))
                .build();

        when(accountRepository.findByAccountNumber(transferResource.getFromAccountNumber())).thenReturn(Optional.of(from));
        when(accountRepository.findByAccountNumber(transferResource.getToAccountNumber())).thenReturn(Optional.of(to));

        // when
        accountService.transfer(transferResource);

        // then
        assertEquals(new BigDecimal("4.01"), from.getBalance());
        assertEquals(new BigDecimal("65.99"), to.getBalance());

        verify(transactionService).createTransferTransactions(from, to, transferResource.getAmount(), Currency.GBP);
        verify(accountRepository).save(from);
        verify(accountRepository).save(to);
    }

    @Test
    void transferWithAccountNotFound() {
        // given
        TransferResource transferResource = TransferResource.builder()
                .fromAccountNumber(123L)
                .toAccountNumber(456L)
                .amount(new BigDecimal("15.99"))
                .build();

        when(accountRepository.findByAccountNumber(transferResource.getFromAccountNumber())).thenReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> accountService.transfer(transferResource));

        // then
        verifyNoMoreInteractions(transactionService, accountRepository);
    }

    @Test
    void transferWithInsufficientBalance() {
        // given
        TransferResource transferResource = TransferResource.builder()
                .fromAccountNumber(123L)
                .toAccountNumber(456L)
                .amount(new BigDecimal("20.99"))
                .build();

        Account from = Account.builder()
                .accountNumber(123L)
                .balance(new BigDecimal("20.00"))
                .build();

        Account to = Account.builder()
                .accountNumber(456L)
                .balance(new BigDecimal("50.00"))
                .build();

        when(accountRepository.findByAccountNumber(transferResource.getFromAccountNumber())).thenReturn(Optional.of(from));
        when(accountRepository.findByAccountNumber(transferResource.getToAccountNumber())).thenReturn(Optional.of(to));

        // when
        assertThrows(InsufficientBalanceException.class, () -> accountService.transfer(transferResource));

        // then
        assertEquals(new BigDecimal("20.00"), from.getBalance());
        assertEquals(new BigDecimal("50.00"), to.getBalance());
        verifyNoInteractions(transactionService);
    }

    @Test
    void getTransactions() {

        // given
        Long accountNumber = 123L;
        LocalDateTime from = LocalDate.parse("2021-01-01", DateTimeFormatter.ISO_DATE).atStartOfDay();
        LocalDateTime to = LocalDate.parse("2021-12-01", DateTimeFormatter.ISO_DATE).atStartOfDay();
        int page = 10;
        int size = 20;
        boolean descending = true;

        // when
        accountService.getTransactions(accountNumber, from, to, page, size, descending);

        // then
        verify(transactionService).getTransactions(eq(accountNumber), eq(from), eq(to), eq(PageRequest.of(page, size, Sort.by("createdAt").descending())));

    }

    @Test
    void getTransactionsByAscOrder() {

        // given
        Long accountNumber = 123L;
        LocalDateTime from = LocalDate.parse("2021-01-01", DateTimeFormatter.ISO_DATE).atStartOfDay();
        LocalDateTime to = LocalDate.parse("2021-12-01", DateTimeFormatter.ISO_DATE).atStartOfDay();
        int page = 10;
        int size = 20;
        boolean descending = false;

        Pageable pageRequest = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        List<TransactionResource> transactionResources = generator.objects(TransactionResource.class, 5).collect(Collectors.toList());
        when(transactionService.getTransactions(eq(accountNumber), eq(from), eq(to), eq(pageRequest)))
                .thenReturn(transactionResources);

        // when
        List<TransactionResource> resources = accountService.getTransactions(accountNumber, from, to, page, size, descending);
        assertEquals(transactionResources, resources);

        // then
        verify(transactionService).getTransactions(eq(accountNumber), eq(from), eq(to), eq(PageRequest.of(page, size, Sort.by("createdAt").ascending())));
    }
}