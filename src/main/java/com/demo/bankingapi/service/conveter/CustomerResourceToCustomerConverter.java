package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.domain.CustomerResource;
import com.demo.bankingapi.entity.Account;
import com.demo.bankingapi.entity.Customer;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerResourceToCustomerConverter implements Converter<CustomerResource, Customer> {

    private final ConversionService conversionService;

    public CustomerResourceToCustomerConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public Customer convert(CustomerResource customerResource) {

        List<Account> accounts = customerResource.getAccounts().stream()
                .map(accountResource -> conversionService.convert(accountResource, Account.class))
                .collect(Collectors.toList());

        Customer customer = Customer.builder()
                .customerNumber(customerResource.getCustomerNumber())
                .name(customerResource.getName())
                .status(Customer.Status.from(customerResource.getStatus()))
                .mobile(customerResource.getMobile())
                .email(customerResource.getEmail())
                .build();
        customer.setAccounts(accounts);
        return customer;
    }
}
