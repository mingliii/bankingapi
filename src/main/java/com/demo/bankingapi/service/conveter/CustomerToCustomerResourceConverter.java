package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.resource.AccountResource;
import com.demo.bankingapi.resource.CustomerResource;
import com.demo.bankingapi.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

// todo test

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
                .map(account -> {
                    AccountResource accountResource = conversionService.convert(account, AccountResource.class);
                    requireNonNull(accountResource).setCustomerNumber(customer.getCustomerNumber());
                    return accountResource;
                })
                .collect(Collectors.toList());

        return CustomerResource.builder()
                .customerNumber(customer.getCustomerNumber())
                .name(customer.getName())
                .email(customer.getEmail())
                .mobile(customer.getMobile())
                .status(customer.getStatus() != null ? customer.getStatus().name() : null)
                .accounts(accounts)
                .build();
    }
}
