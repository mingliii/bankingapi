package com.demo.bankingapi.service;

import com.demo.bankingapi.resource.CustomerResource;
import com.demo.bankingapi.entity.Customer;
import com.demo.bankingapi.repository.CustomerRepository;
import com.demo.bankingapi.service.exception.NotFoundException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

// todo test

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final ConversionService conversionService;

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

        throw new NotFoundException(Customer.class, customerNumber);
    }

    public CustomerResource createCustomer(CustomerResource customerResource) {
        Customer customer = conversionService.convert(customerResource, Customer.class);
        customer = customerRepository.save(requireNonNull(customer));
        return conversionService.convert(customer, CustomerResource.class);
    }

    public CustomerResource updateCustomer(CustomerResource customerResource) {
        Long customerNumber = customerResource.getCustomerNumber();
        if (customerNumber == null) {
            throw new IllegalArgumentException("Customer number must be provided");
        }

        Optional<Customer> customerOptional = customerRepository.findByCustomerNumber(customerNumber);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setEmail(customerResource.getEmail());
            customer.setMobile(customerResource.getMobile());
            customer.setName(customerResource.getName());
            customerRepository.save(requireNonNull(customer));
        }

        throw new NotFoundException(Customer.class, customerNumber);
    }

    public void deleteCustomer(Long customerNumber) {
        Optional<Customer> customerOptional = customerRepository.findById(customerNumber);
        customerOptional.ifPresentOrElse(customerRepository::delete, () -> {
            throw new NotFoundException(Customer.class, customerNumber);
        });
    }
}
