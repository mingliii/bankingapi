package com.demo.bankingapi.service;

import com.demo.bankingapi.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ConversionService conversionService;

    @Test
    void getAllCustomers() {
        // given

        // when

        // then
    }

    @Test
    void getCustomer() {
        // given

        // when

        // then
    }

    @Test
    void createCustomer() {
        // given

        // when

        // then
    }

    @Test
    void updateCustomer() {
        // given

        // when

        // then
    }

    @Test
    void deleteCustomer() {
        // given

        // when

        // then
    }
}