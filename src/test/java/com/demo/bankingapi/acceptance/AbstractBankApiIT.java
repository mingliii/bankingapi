package com.demo.bankingapi.acceptance;

import com.demo.bankingapi.entity.Currency;
import com.demo.bankingapi.repository.AccountRepository;
import com.demo.bankingapi.repository.CustomerRepository;
import com.demo.bankingapi.repository.TransactionRepository;
import com.demo.bankingapi.resource.AccountResource;
import com.demo.bankingapi.resource.TransactionResource;
import com.demo.bankingapi.resource.TransferResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public abstract class AbstractBankApiIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected TransactionRepository transactionRepository;

    @Autowired
    protected ObjectMapper objectMapper;


    @SneakyThrows
    protected AccountResource createAccount(AccountResource accountResource) {
        String primaryAccountString = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountResource)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn()
                .getResponse().getContentAsString();
        return objectMapper.readValue(primaryAccountString, AccountResource.class);
    }

    @SneakyThrows
    protected AccountResource retrieveAccount(Long accountNumber) {
        String accountString = mockMvc.perform(get("/accounts/" + accountNumber))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse().getContentAsString();
        return objectMapper.readValue(accountString, AccountResource.class);
    }

    @SneakyThrows
    protected List<TransactionResource> retrieveTransactions(Long accountNumber, String type) {
        String primaryTransactionString = mockMvc.perform(get("/accounts/" + accountNumber + "/transactions").queryParam("type", type))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse().getContentAsString();
        return asList(objectMapper.readValue(primaryTransactionString, TransactionResource[].class));
    }

    @SneakyThrows
    protected void transfer(Long from, Long to, BigDecimal amount) {
        TransferResource transferResource = TransferResource.builder()
                .fromAccountNumber(from)
                .toAccountNumber(to)
                .amount(amount)
                .build();

        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferResource)))
                .andDo(print());
    }

    protected AccountResource createAccountResource(BigDecimal amount, Long customerNumber) {
        return AccountResource.builder()
                .balance(amount)
                .currency(Currency.GBP)
                .customerNumber(customerNumber)
                .status("ACTIVE")
                .type("DEBIT").build();
    }
}
