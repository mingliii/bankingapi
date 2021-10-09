package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.domain.CustomerResource;
import com.demo.bankingapi.entity.Customer;
import org.springframework.core.convert.converter.Converter;

public class CustomerResourceToCustomerConverter implements Converter<CustomerResource, Customer> {

    @Override
    public Customer convert(CustomerResource customerResource) {
        return Customer.builder()
                .customerNumber(customerResource.getCustomerNumber())
                .name(customerResource.getName())
                .status(customerResource.getStatus())
                .mobile(customerResource.getMobile())
                .email(customerResource.getEmail())
                .build();
    }
}
