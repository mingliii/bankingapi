package com.demo.bankingapi.controller;

import com.demo.bankingapi.resource.CustomerResource;
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

    /**
     * Create customer endpoint that allows customer created with initial accounts.
     * @param customer customer represented by {@link CustomerResource}
     * @return created customer represented by {@link CustomerResource}
     */
    @PostMapping
    public CustomerResource createCustomer(@Valid @RequestBody CustomerResource customer) {
        return customerService.createCustomer(customer);
    }

    @GetMapping(path = "/{customerNumber}")
    public CustomerResource getCustomer(@PathVariable Long customerNumber) {
        return customerService.getCustomer(customerNumber);
    }

    @PutMapping(path = "/{customerNumber}")
    public CustomerResource updateCustomer(@RequestBody CustomerResource customer, @PathVariable Long customerNumber) {
        customer.setCustomerNumber(customerNumber);
        return customerService.updateCustomer(customer);
    }

    @DeleteMapping(path = "/{customerNumber}")
    public void deleteCustomer(@PathVariable Long customerNumber) {
        customerService.deleteCustomer(customerNumber);
    }
}
