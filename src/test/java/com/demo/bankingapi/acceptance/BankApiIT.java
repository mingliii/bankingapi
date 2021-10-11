package com.demo.bankingapi.acceptance;

import com.demo.bankingapi.entity.Currency;
import com.demo.bankingapi.entity.Customer;
import com.demo.bankingapi.repository.AccountRepository;
import com.demo.bankingapi.repository.CustomerRepository;
import com.demo.bankingapi.repository.TransactionRepository;
import com.demo.bankingapi.resource.AccountResource;
import com.demo.bankingapi.resource.TransactionResource;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
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
        // create 1st account
        String primaryAccountString = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(primaryAccount)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn()
                .getResponse().getContentAsString();
        AccountResource primaryAccountCreated = objectMapper.readValue(primaryAccountString, AccountResource.class);

        // retrieve 1st transaction
        String primaryTransactionString = mockMvc.perform(get("/accounts/" + primaryAccountCreated.getAccountNumber() + "/transactions"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse().getContentAsString();
        List<TransactionResource> primaryTransactions = asList(objectMapper.readValue(primaryTransactionString, TransactionResource[].class));

        // create 2nd account
        String secondaryAccountString = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondaryAccount)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn()
                .getResponse().getContentAsString();
        AccountResource secondaryAccountCreated = objectMapper.readValue(secondaryAccountString, AccountResource.class);

        // retrieve 2nd transaction
        String secondaryTransactionString = mockMvc.perform(get("/accounts/" + secondaryAccountCreated.getAccountNumber() + "/transactions"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse().getContentAsString();
        List<TransactionResource> secondaryTransactions = asList(objectMapper.readValue(secondaryTransactionString, TransactionResource[].class));

        // then
        assertEquals(2, accountRepository.findByCustomer_CustomerNumber(customerId).size());

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

    @Test
    void testTransfer() {
    }

    @Test
    void testTransferWithInsufficientBalance() {

    }
}