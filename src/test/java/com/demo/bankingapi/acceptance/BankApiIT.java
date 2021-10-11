package com.demo.bankingapi.acceptance;

import com.demo.bankingapi.entity.Currency;
import com.demo.bankingapi.entity.Customer;
import com.demo.bankingapi.entity.Transaction;
import com.demo.bankingapi.repository.AccountRepository;
import com.demo.bankingapi.repository.CustomerRepository;
import com.demo.bankingapi.repository.TransactionRepository;
import com.demo.bankingapi.resource.AccountResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
@SuppressWarnings("OptionalGetWithoutIsPresent")
class BankApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        // 4 customers have been set up initially in db.
        List<Customer> customers = customerRepository.findAll();
        assertEquals(4, customers.size());
        assertTrue(customers.stream().map(Customer::getCustomerNumber).collect(toUnmodifiableList()).containsAll(List.of(1L, 2L, 3L, 4L)));
    }

    @SneakyThrows
    @Test
    void testCreateAccountsForCustomer() {
        // given
        Long customerId = 1L;

        AccountResource primaryAccount = AccountResource.builder()
                .balance(new BigDecimal("100.00"))
                .currency(Currency.GBP)
                .customerNumber(customerId)
                .status("ACTIVE")
                .type("DEBIT").build();

        AccountResource secondaryAccount = AccountResource.builder()
                .balance(new BigDecimal("300.00"))
                .currency(Currency.GBP)
                .customerNumber(customerId)
                .status("ACTIVE")
                .type("DEBIT").build();

        // when
        // create an account
        String primaryAccountString = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(primaryAccount)))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse().getContentAsString();
        AccountResource primaryAccountCreated = objectMapper.readValue(primaryAccountString, AccountResource.class);
        // create an account
        String secondaryAccountString = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondaryAccount)))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse().getContentAsString();
        AccountResource secondaryAccountCreated = objectMapper.readValue(secondaryAccountString, AccountResource.class);

        // then
        BigDecimal primaryAccountBalance = accountRepository.findByAccountNumber(primaryAccountCreated.getAccountNumber()).get().getBalance();
        BigDecimal secondaryAccountBalance = accountRepository.findByAccountNumber(secondaryAccountCreated.getAccountNumber()).get().getBalance();

        Transaction primaryAccountDepositTransaction = transactionRepository.findAllByAccount_AccountNumber(primaryAccountCreated.getAccountNumber()).get(0);
        Transaction secondaryAccountDepositTransaction = transactionRepository.findAllByAccount_AccountNumber(secondaryAccountCreated.getAccountNumber()).get(0);

        assertEquals(2, accountRepository.findByCustomer_CustomerNumber(customerId).size());

        assertEquals(new BigDecimal("100.00"), primaryAccountBalance);
        assertEquals(Transaction.Type.DEPOSIT, primaryAccountDepositTransaction.getType());
        assertEquals(new BigDecimal("100.00"), primaryAccountDepositTransaction.getInAmount());
        assertEquals(new BigDecimal("100.00"), primaryAccountDepositTransaction.getBalance());

        assertEquals(new BigDecimal("300.00"), secondaryAccountBalance);
        assertEquals(Transaction.Type.DEPOSIT, secondaryAccountDepositTransaction.getType());
        assertEquals(new BigDecimal("300.00"), secondaryAccountDepositTransaction.getInAmount());
        assertEquals(new BigDecimal("300.00"), secondaryAccountDepositTransaction.getBalance());
    }

    @Test
    void testRetrieveAccountBalance() {

    }

    @Test
    void testTransferBetweenDifferentCustomers() {
    }

    @Test
    void testTransferBetweenDifferentAccountsForSameCustomer() {

    }

    @Test
    void testRetrieveAccountTransactions() {

    }
}