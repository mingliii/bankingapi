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
class CustomerToCustomerResourceConverterTest {

    @Mock
    private ConversionService conversionService;

    private CustomerToCustomerResourceConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CustomerToCustomerResourceConverter(conversionService);
    }

    @Test
    void testConverter() {

        // given
        Account account = mock(Account.class);
        when(conversionService.convert(account, AccountResource.class)).thenReturn(mock(AccountResource.class));

        Customer customer = Customer.builder()
                .customerNumber(200L)
                .email("test@test.com")
                .mobile("07800000000")
                .name("Tester Tester")
                .status(Customer.Status.ACTIVE)
                .accounts(Collections.singletonList(account))
                .build();

        // when
        CustomerResource customerResource = converter.convert(customer);

        // then
        verify(conversionService).convert(account, AccountResource.class);
        assertNotNull(customerResource);
        assertEquals(200L, customerResource.getCustomerNumber());
        assertEquals("test@test.com", customerResource.getEmail());
        assertEquals("07800000000", customerResource.getMobile());
        assertEquals("Tester Tester", customerResource.getName());
        assertEquals("ACTIVE", customerResource.getStatus());
    }
}