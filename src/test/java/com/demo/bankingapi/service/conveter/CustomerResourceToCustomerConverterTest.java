package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.entity.Account;
import com.demo.bankingapi.entity.Customer;
import com.demo.bankingapi.resource.AccountResource;
import com.demo.bankingapi.resource.CustomerResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerResourceToCustomerConverterTest {

    @Mock
    private ConversionService conversionService;

    private CustomerResourceToCustomerConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CustomerResourceToCustomerConverter(conversionService);
    }

    @Test
    void testConverter() {

        // given
        AccountResource accountResource = mock(AccountResource.class);
        when(conversionService.convert(accountResource, Account.class)).thenReturn(mock(Account.class));

        CustomerResource customerResource = CustomerResource.builder()
                .customerNumber(200L)
                .email("test@test.com")
                .mobile("07800000000")
                .name("Tester Tester")
                .status("ACTIVE")
                .accounts(Collections.singletonList(accountResource))
                .build();

        // when
        Customer custmer = converter.convert(customerResource);

        // then
        verify(conversionService).convert(accountResource, Account.class);
        assertNotNull(custmer);
        assertEquals(200L, custmer.getCustomerNumber());
        assertEquals("test@test.com", custmer.getEmail());
        assertEquals("07800000000", custmer.getMobile());
        assertEquals("Tester Tester", custmer.getName());
        assertEquals(Customer.Status.ACTIVE, custmer.getStatus());
    }
}