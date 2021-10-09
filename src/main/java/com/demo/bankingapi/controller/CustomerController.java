package com.demo.bankingapi.controller;

import com.demo.bankingapi.domain.CustomerResource;
import com.demo.bankingapi.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerResource> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PostMapping
    public CustomerResource createCustomer(@Valid @RequestBody CustomerResource customer,
                                        @RequestParam(required = false, defaultValue = "true") boolean createAccount) {
        return customerService.createCustomer(customer, createAccount);
    }

    @GetMapping(path = "/{customerNumber}")
    public CustomerResource getCustomer(@PathVariable Long customerNumber) {
        return customerService.getCustomer(customerNumber);
    }

    @PutMapping(path = "/{customerNumber}")
    public CustomerResource updateCustomer(@RequestBody CustomerResource customer) {
        return customerService.updateCustomer(customer);
    }

    @DeleteMapping(path = "/{customerNumber}")
    public void deleteCustomer(@PathVariable Long customerNumber) {
        customerService.deleteCustomer(customerNumber);
    }
}
