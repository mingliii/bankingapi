package com.demo.bankingapi.acceptance;

import com.demo.bankingapi.entity.Customer;
import com.demo.bankingapi.resource.AccountResource;
import com.demo.bankingapi.resource.TransactionResource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class BankApiIT extends AbstractBankApiIT {

    @BeforeEach
    public void setUp() {
        // 4 customers have been set up initially in db.
        List<Customer> customers = customerRepository.findAll();
        assertEquals(4, customers.size());
        assertTrue(customers.stream().map(Customer::getCustomerNumber).collect(toUnmodifiableList()).containsAll(List.of(1L, 2L, 3L, 4L)));
    }

    @SneakyThrows
    @Test
    void testCreateAccountsForSameCustomer() {
        // given
        Long customerNumber = 1L;

        // when
        // create 1st account
        AccountResource primaryAccountCreated = createAccount(createAccountResource(new BigDecimal("100.00"), customerNumber));
        // create 2nd account
        AccountResource secondaryAccountCreated = createAccount(createAccountResource(new BigDecimal("300.00"), customerNumber));

        // retrieve 1st transaction
        List<TransactionResource> primaryTransactions = retrieveTransactions(primaryAccountCreated.getAccountNumber(), "DEPOSIT");
        // retrieve 2nd transaction
        List<TransactionResource> secondaryTransactions = retrieveTransactions(secondaryAccountCreated.getAccountNumber(), "DEPOSIT");

        // then
        assertEquals(2, accountRepository.findByCustomer_CustomerNumber(customerNumber).size());

        assertEquals(new BigDecimal("100.00"), primaryAccountCreated.getBalance());
        assertEquals(1, primaryTransactions.size());
        assertEquals("DEPOSIT", primaryTransactions.get(0).getType());
        assertEquals(new BigDecimal("100.00"), primaryTransactions.get(0).getInAmount());
        assertEquals(BigDecimal.ZERO, primaryTransactions.get(0).getOutAmount());
        assertEquals(new BigDecimal("100.00"), primaryTransactions.get(0).getBalance());

        assertEquals(new BigDecimal("300.00"), secondaryAccountCreated.getBalance());
        assertEquals(1, secondaryTransactions.size());
        assertEquals("DEPOSIT", secondaryTransactions.get(0).getType());
        assertEquals(new BigDecimal("300.00"), secondaryTransactions.get(0).getInAmount());
        assertEquals(BigDecimal.ZERO, secondaryTransactions.get(0).getOutAmount());
        assertEquals(new BigDecimal("300.00"), secondaryTransactions.get(0).getBalance());
    }

    @SneakyThrows
    @Test
    void testCreateAccountsForDifferentCustomers() {
        // given
        Long firstCustomer = 1L;
        Long secondCustomer = 2L;

        // when
        // create 1st account
        AccountResource firstAccount = createAccount(createAccountResource(new BigDecimal("100.00"), firstCustomer));
        // create 2nd account
        AccountResource secondAccount = createAccount(createAccountResource(new BigDecimal("300.00"), secondCustomer));

        // retrieve 1st transaction
        List<TransactionResource> firstTransactions = retrieveTransactions(firstAccount.getAccountNumber(), "DEPOSIT");
        // retrieve 2nd transaction
        List<TransactionResource> secondTransactions = retrieveTransactions(secondAccount.getAccountNumber(), "DEPOSIT");

        // then
        assertEquals(1, accountRepository.findByCustomer_CustomerNumber(firstCustomer).size());
        assertEquals(1, accountRepository.findByCustomer_CustomerNumber(secondCustomer).size());

        assertEquals(new BigDecimal("100.00"), firstAccount.getBalance());
        assertEquals(1, firstTransactions.size());
        assertEquals("DEPOSIT", firstTransactions.get(0).getType());
        assertEquals(new BigDecimal("100.00"), firstTransactions.get(0).getInAmount());
        assertEquals(BigDecimal.ZERO, firstTransactions.get(0).getOutAmount());
        assertEquals(new BigDecimal("100.00"), firstTransactions.get(0).getBalance());

        assertEquals(new BigDecimal("300.00"), secondAccount.getBalance());
        assertEquals(1, secondTransactions.size());
        assertEquals("DEPOSIT", secondTransactions.get(0).getType());
        assertEquals(new BigDecimal("300.00"), secondTransactions.get(0).getInAmount());
        assertEquals(BigDecimal.ZERO, secondTransactions.get(0).getOutAmount());
        assertEquals(new BigDecimal("300.00"), secondTransactions.get(0).getBalance());
    }

    @SneakyThrows
    @Test
    void testTransfer() {
        // given
        // create 1st account
        AccountResource fromAccount = createAccount(createAccountResource(new BigDecimal("100.00"), 1L));
        // create 2nd account
        AccountResource toAccount = createAccount(createAccountResource(new BigDecimal("300.00"), 2L));

        // when
        transfer(fromAccount.getAccountNumber(), toAccount.getAccountNumber(), new BigDecimal("50.99"));
        transfer(fromAccount.getAccountNumber(), toAccount.getAccountNumber(), new BigDecimal("10.99"));

        List<TransactionResource> fromTfsTransactions = retrieveTransactions(fromAccount.getAccountNumber(), "TRANSFER");
        List<TransactionResource> toTfsTransactions = retrieveTransactions(toAccount.getAccountNumber(), "TRANSFER");

        fromAccount = retrieveAccount(fromAccount.getAccountNumber());
        toAccount = retrieveAccount(toAccount.getAccountNumber());

        // then
        assertEquals(new BigDecimal("38.02"), fromAccount.getBalance());
        assertEquals(new BigDecimal("361.98"), toAccount.getBalance());

        assertEquals(2, fromTfsTransactions.size());
        assertEquals(new BigDecimal("38.02"), fromTfsTransactions.get(0).getBalance());
        assertEquals(new BigDecimal("49.01"), fromTfsTransactions.get(1).getBalance());

        assertEquals(2, toTfsTransactions.size());
        assertEquals(new BigDecimal("361.98"), toTfsTransactions.get(0).getBalance());
        assertEquals(new BigDecimal("350.99"), toTfsTransactions.get(1).getBalance());
    }

    @Test
    void testTransferWithInsufficientBalance() {
// given
        // create 1st account
        AccountResource fromAccount = createAccount(createAccountResource(new BigDecimal("100.00"), 1L));
        // create 2nd account
        AccountResource toAccount = createAccount(createAccountResource(new BigDecimal("300.00"), 2L));

        // when
        transfer(fromAccount.getAccountNumber(), toAccount.getAccountNumber(), new BigDecimal("50.99"));
        transfer(fromAccount.getAccountNumber(), toAccount.getAccountNumber(), new BigDecimal("50.00"));

        List<TransactionResource> fromTfsTransactions = retrieveTransactions(fromAccount.getAccountNumber(), "TRANSFER");
        List<TransactionResource> toTfsTransactions = retrieveTransactions(toAccount.getAccountNumber(), "TRANSFER");

        fromAccount = retrieveAccount(fromAccount.getAccountNumber());
        toAccount = retrieveAccount(toAccount.getAccountNumber());

        // then
        assertEquals(new BigDecimal("49.01"), fromAccount.getBalance());
        assertEquals(new BigDecimal("350.99"), toAccount.getBalance());

        assertEquals(1, fromTfsTransactions.size());
        assertEquals(new BigDecimal("49.01"), fromTfsTransactions.get(0).getBalance());

        assertEquals(1, toTfsTransactions.size());
        assertEquals(new BigDecimal("350.99"), toTfsTransactions.get(0).getBalance());
    }
}