package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.entity.Account;
import com.demo.bankingapi.entity.Currency;
import com.demo.bankingapi.resource.AccountResource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountResourceToAccountConverterTest {

    private final AccountResourceToAccountConverter converter = new AccountResourceToAccountConverter();

    @Test
    void testConvert() {

        // given
        AccountResource resource = AccountResource.builder()
                .accountNumber(100L)
                .balance(BigDecimal.TEN)
                .type("DEBIT")
                .status("ACTIVE")
                .currency(Currency.GBP)
                .customerNumber(200L)
                .build();

        // when
        Account account = converter.convert(resource);

        // then
        assertNotNull(account);
        assertEquals(100L, account.getAccountNumber());
        assertEquals(BigDecimal.TEN, account.getBalance());
        assertEquals(Account.Type.DEBIT, account.getType());
        assertEquals(Currency.GBP, account.getCurrency());
        assertEquals(Account.Status.ACTIVE, account.getStatus());
    }

    @Test
    void testConvertWithoutTypeAndStatus() {

        // given
        AccountResource resource = AccountResource.builder()
                .accountNumber(100L)
                .balance(BigDecimal.TEN)
                .customerNumber(200L)
                .currency(Currency.GBP)
                .build();

        // when
        Account account = converter.convert(resource);

        // then
        assertNotNull(account);
        assertEquals(100L, account.getAccountNumber());
        assertEquals(BigDecimal.TEN, account.getBalance());
        assertEquals(Account.Type.DEBIT, account.getType());
        assertEquals(Currency.GBP, account.getCurrency());
        assertEquals(Account.Status.ACTIVE, account.getStatus());
    }
}