package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.entity.Account;
import com.demo.bankingapi.entity.Currency;
import com.demo.bankingapi.entity.Transaction;
import com.demo.bankingapi.resource.TransactionResource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionToTransactionResourceConverterTest {

    private final TransactionToTransactionResourceConverter converter = new TransactionToTransactionResourceConverter();

    @Test
    void testConvert() {

        // given
        Account account = Mockito.mock(Account.class);
        Mockito.when(account.getAccountNumber()).thenReturn(200L);

        Transaction transaction = Transaction.builder()
                .id(100L)
                .account(account)
                .balance(new BigDecimal("250.50"))
                .inAmount(new BigDecimal("50.50"))
                .outAmount(BigDecimal.ZERO)
                .type(Transaction.Type.TRANSFER)
                .currency(Currency.GBP)
                .description("a test transaction")
                .createdAt(LocalDateTime.parse("2021-10-10T10:15:30", DateTimeFormatter.ISO_DATE_TIME))
                .build();

        // when
        TransactionResource transactionResource = converter.convert(transaction);

        // then
        assertNotNull(transactionResource);
        assertEquals(100L, transactionResource.getId());
        assertEquals(200L, transactionResource.getAccountNumber());
        assertEquals(new BigDecimal("250.50"), transactionResource.getBalance());
        assertEquals(new BigDecimal("50.50"), transactionResource.getInAmount());
        assertEquals(BigDecimal.ZERO, transactionResource.getOutAmount());
        assertEquals("GBP", transactionResource.getCurrency());
        assertEquals("a test transaction", transactionResource.getDescription());
        assertEquals(LocalDateTime.parse("2021-10-10T10:15:30", DateTimeFormatter.ISO_DATE_TIME), transactionResource.getCreatedAt());
    }
}