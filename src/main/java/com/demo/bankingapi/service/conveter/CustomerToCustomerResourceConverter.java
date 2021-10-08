package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.domain.AccountResource;
import com.demo.bankingapi.domain.CustomerResource;
import com.demo.bankingapi.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerToCustomerResourceConverter implements Converter<Customer, CustomerResource> {

    private final ConversionService conversionService;

    @Autowired
    public CustomerToCustomerResourceConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public CustomerResource convert(Customer customer) {
        List<AccountResource> accounts = customer.getAccounts().stream()
                .map(account -> conversionService.convert(account, AccountResource.class))
                .collect(Collectors.toList());

        return CustomerResource.builder()
                .customerNumber(customer.getCustomerNumber())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .status(customer.getStatus())
                .accounts(accounts)
                .build();
    }
}
