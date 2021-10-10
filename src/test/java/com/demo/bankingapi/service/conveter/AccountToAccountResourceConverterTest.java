package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.entity.Account;
import com.demo.bankingapi.entity.Customer;
import com.demo.bankingapi.resource.AccountResource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountToAccountResourceConverterTest {

    private final AccountToAccountResourceConverter converter = new AccountToAccountResourceConverter();

    @Test
    void testConverter() {

        // given
        Account account = Account.builder()
                .accountNumber(100L)
                .balance(BigDecimal.TEN)
                .type(Account.Type.DEBIT)
                .status(Account.Status.ACTIVE)
                .customer(Customer.builder().customerNumber(200L).build())
                .build();

        // when
        AccountResource resource = converter.convert(account);

        // then
        assertNotNull(resource);
        assertEquals(100L, resource.getAccountNumber());
        assertEquals(BigDecimal.TEN, resource.getBalance());
        assertEquals("DEBIT", resource.getType());
        assertEquals("ACTIVE", resource.getStatus());
        assertEquals(200L, resource.getCustomerNumber());
    }
}