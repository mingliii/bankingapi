package com.demo.bankingapi.controller;

import com.demo.bankingapi.resource.CustomerResource;
import com.demo.bankingapi.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customers")
@Api(tags = { "Customer REST endpoints" })
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @ApiOperation(value = "Get all customers", notes = "Get all customers")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public List<CustomerResource> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    /**
     * Create customer endpoint that allows customer created with initial accounts.
     * @param customer customer represented by {@link CustomerResource}
     * @return created customer represented by {@link CustomerResource}
     */
    @PostMapping
    @ApiOperation(value = "Create customer", notes = "Create customer with provided information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public CustomerResource createCustomer(@Valid @RequestBody CustomerResource customer) {
        return customerService.createCustomer(customer);
    }

    @GetMapping(path = "/{customerNumber}")
    @ApiOperation(value = "Get customer by customer number", notes = "Get customer by customer number")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public CustomerResource getCustomer(@PathVariable Long customerNumber) {
        return customerService.getCustomer(customerNumber);
    }

    @PutMapping(path = "/{customerNumber}")
    @ApiOperation(value = "Update customer", notes = "Update customer with provided information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public CustomerResource updateCustomer(@RequestBody CustomerResource customer, @PathVariable Long customerNumber) {
        customer.setCustomerNumber(customerNumber);
        return customerService.updateCustomer(customer);
    }

    @DeleteMapping(path = "/{customerNumber}")
    @ApiOperation(value = "Delete customer", notes = "Delete customer by customer number")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public void deleteCustomer(@PathVariable Long customerNumber) {
        customerService.deleteCustomer(customerNumber);
    }
}
