package com.demo.bankingapi.service;

import com.demo.bankingapi.entity.Customer;
import com.demo.bankingapi.repository.CustomerRepository;
import com.demo.bankingapi.resource.CustomerResource;
import com.demo.bankingapi.service.exception.NotFoundException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ConversionService conversionService;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void getAllCustomers() {
        // given
        List<Customer> customers = generator.objects(Customer.class, 5).collect(Collectors.toList());
        when(customerRepository.findAll()).thenReturn(customers);

        // when
        customerService.getAllCustomers();

        // then
        verify(conversionService, times(5)).convert(any(Customer.class), eq(CustomerResource.class));
    }

    @Test
    void getCustomer() {
        // given
        Long customerNumber = 123L;
        Customer customer = mock(Customer.class);
        when(customerRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.of(customer));

        // when
        customerService.getCustomer(customerNumber);

        // then
        verify(customerRepository).findByCustomerNumber(customerNumber);
        verify(conversionService).convert(customer, CustomerResource.class);
    }

    @Test
    void getCustomerNotFound() {
        // given
        Long customerNumber = 123L;
        when(customerRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.empty());

        // when
        Assertions.assertThrows(NotFoundException.class, () -> customerService.getCustomer(customerNumber));

        // then
        verify(customerRepository).findByCustomerNumber(customerNumber);
        Mockito.verifyNoInteractions(conversionService);
    }

    @Test
    void createCustomer() {
        // given
        CustomerResource customerResource = mock(CustomerResource.class);
        Customer customer = mock(Customer.class);
        Customer savedCustomer = mock(Customer.class);
        when(conversionService.convert(customerResource, Customer.class)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(savedCustomer);
        // when
        customerService.createCustomer(customerResource);

        // then
        verify(customerRepository).save(customer);
        verify(conversionService).convert(savedCustomer, CustomerResource.class);
    }

    @Test
    void updateCustomer() {
        // given
        Long customerNumber = 123L;
        CustomerResource customerResource = CustomerResource.builder()
                .customerNumber(customerNumber)
                .name("Tester Tester")
                .email("tester@tester.com")
                .mobile("07800000000")
                .build();
        Customer customer = generator.nextObject(Customer.class);
        Customer savedCustomer = generator.nextObject(Customer.class);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        when(customerRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(savedCustomer);

        // when
        customerService.updateCustomer(customerResource);

        // then
        verify(customerRepository).findByCustomerNumber(customerNumber);
        verify(conversionService).convert(savedCustomer, CustomerResource.class);
        verify(customerRepository).save(customerArgumentCaptor.capture());

        Assertions.assertEquals("Tester Tester", customerArgumentCaptor.getValue().getName());
        Assertions.assertEquals("tester@tester.com", customerArgumentCaptor.getValue().getEmail());
        Assertions.assertEquals("07800000000", customerArgumentCaptor.getValue().getMobile());
        Assertions.assertEquals("Tester Tester", customerArgumentCaptor.getValue().getName());
    }

    @Test
    void updateCustomerNotFound() {
        // given
        Long customerNumber = 123L;
        CustomerResource customerResource = CustomerResource.builder()
                .customerNumber(customerNumber)
                .name("Tester Tester")
                .email("tester@tester.com")
                .mobile("07800000000")
                .build();
        when(customerRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.empty());

        // when
        Assertions.assertThrows(NotFoundException.class, () -> customerService.updateCustomer(customerResource));

        // then
        verifyNoMoreInteractions(customerRepository, conversionService);
    }

    @Test
    void deleteCustomer() {
        // given
        Long customerNumber = 123L;
        Customer customer = mock(Customer.class);
        when(customerRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.of(customer));

        // when
        customerService.deleteCustomer(customerNumber);

        // then
        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomerNotFound() {
        // given
        Long customerNumber = 123L;
        when(customerRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.empty());

        // when
        Assertions.assertThrows(NotFoundException.class, () -> customerService.deleteCustomer(customerNumber));

        // then
        verifyNoMoreInteractions(customerRepository);
    }
}