package com.demo.bankingapi.controller;

import com.demo.bankingapi.domain.CustomerResource;
import com.demo.bankingapi.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping(path = "/all")
    public List<CustomerResource> getAllCustomers() {
        // todo
        return null;
    }

    @PostMapping(path = "/add")
    public void addCustomer(@RequestBody CustomerResource customer) {
        // todo
    }

    @GetMapping(path = "/{customerNumber}")
    public CustomerResource getCustomer(@PathVariable Long customerNumber) {
        // todo
        return null;
    }

    @PutMapping(path = "/{customerNumber}")
    public void updateCustomer(@RequestBody CustomerResource customer) {
        // todo
    }

    @DeleteMapping(path = "/{customerNumber}")
    public void deleteCustomer(@PathVariable Long customerNumber) {
        // todo
    }
}
