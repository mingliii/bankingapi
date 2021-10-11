package com.demo.bankingapi.service;

import com.demo.bankingapi.resource.CustomerResource;
import com.demo.bankingapi.entity.Customer;
import com.demo.bankingapi.repository.CustomerRepository;
import com.demo.bankingapi.service.exception.NotFoundException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final ConversionService conversionService;

    public CustomerService(CustomerRepository customerRepository, ConversionService conversionService) {
        this.customerRepository = customerRepository;
        this.conversionService = conversionService;
    }

    @Transactional(readOnly = true)
    public List<CustomerResource> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customer -> conversionService.convert(customer, CustomerResource.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResource getCustomer(Long customerNumber) {
        Optional<Customer> customerOptional = customerRepository.findByCustomerNumber(customerNumber);
        if (customerOptional.isEmpty()) {
            throw new NotFoundException(Customer.class, customerNumber);
        }

        return conversionService.convert(customerOptional.get(), CustomerResource.class);
    }

    public CustomerResource createCustomer(CustomerResource customerResource) {
        Customer customer = conversionService.convert(customerResource, Customer.class);
        customer = customerRepository.save(requireNonNull(customer));
        return conversionService.convert(customer, CustomerResource.class);
    }

    @Transactional
    public CustomerResource updateCustomer(CustomerResource customerResource) {
        Long customerNumber = customerResource.getCustomerNumber();
        if (customerNumber == null) {
            throw new IllegalArgumentException("Customer number must be provided");
        }

        Optional<Customer> customerOptional = customerRepository.findByCustomerNumber(customerNumber);

        if (customerOptional.isEmpty()) {
            throw new NotFoundException(Customer.class, customerNumber);
        }

        Customer customer = customerOptional.get();
        customer.setEmail(customerResource.getEmail());
        customer.setMobile(customerResource.getMobile());
        customer.setName(customerResource.getName());
        customer = customerRepository.save(requireNonNull(customer));
        return conversionService.convert(customer, CustomerResource.class);

    }

    @Transactional
    public void deleteCustomer(Long customerNumber) {
        Optional<Customer> customerOptional = customerRepository.findByCustomerNumber(customerNumber);
        customerOptional.ifPresentOrElse(customerRepository::delete, () -> {
            throw new NotFoundException(Customer.class, customerNumber);
        });
    }
}
