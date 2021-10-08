package com.demo.bankingapi.service;

import com.demo.bankingapi.domain.CustomerResource;
import com.demo.bankingapi.entity.Customer;
import com.demo.bankingapi.repository.CustomerRepository;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final ConversionService conversionService;

    private AccountService accountService;

    public CustomerService(CustomerRepository customerRepository, ConversionService conversionService) {
        this.customerRepository = customerRepository;
        this.conversionService = conversionService;
    }

    public List<CustomerResource> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customer -> conversionService.convert(customer, CustomerResource.class))
                .collect(Collectors.toList());
    }

    public CustomerResource getCustomer(Long customerNumber) {
        Optional<Customer> customerOptional = customerRepository.findByCustomerNumber(customerNumber);
        if (customerOptional.isPresent()) {
            return conversionService.convert(customerOptional.get(), CustomerResource.class);
        }

        throw new RuntimeException("Customer: " + customerNumber + " not found");
    }

    public CustomerResource createCustomer(CustomerResource customerResource) {
        return createCustomer(customerResource, false);
    }

    public CustomerResource createCustomer(CustomerResource customerResource, boolean createAccount) {
        Customer customer = conversionService.convert(customerResource, Customer.class);
        customer = customerRepository.save(requireNonNull(customer));
        return conversionService.convert(customer, CustomerResource.class);
    }

    public void deleteCustomer(Long customerNumber) {
        Optional<Customer> customerOptional = customerRepository.findById(customerNumber);
        customerOptional.ifPresentOrElse(customerRepository::delete, () -> {
            throw new RuntimeException("Customer: " + customerNumber + " not found");
        });
    }
}
