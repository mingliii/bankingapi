package com.demo.bankingapi.controller;

import com.demo.bankingapi.resource.AccountResource;
import com.demo.bankingapi.resource.TransactionResource;
import com.demo.bankingapi.resource.TransferResource;
import com.demo.bankingapi.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/accounts")
@Api(tags = {"Account and Transaction REST endpoints"})
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @ApiOperation(value = "Get all accounts", notes = "Get all accounts")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public List<AccountResource> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping(path = "/{accountNumber}")
    @ApiOperation(value = "Get account", notes = "Find accounts by account number")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public AccountResource getByAccountNumber(@PathVariable Long accountNumber) {
        return accountService.getByAccountNumber(accountNumber);
    }

    @ApiOperation(value = "Get account transactions", notes = "Find account transactions by account number")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @GetMapping(path = "/{accountNumber}/transactions")
    public List<TransactionResource> getTransactions(@PathVariable Long accountNumber,
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                     @RequestParam(required = false, defaultValue = "1970-01-01") LocalDate from,
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                     @RequestParam(required = false, defaultValue = "2099-12-31") LocalDate to,
                                                     @RequestParam(defaultValue = "TRANSFER") String type,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size,
                                                     @RequestParam(defaultValue = "true") boolean desc) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from date must be before to date");
        }

        return accountService.getTransactions(accountNumber, type, from.atStartOfDay(), to.plusDays(1).atStartOfDay(), page, size, desc);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create account", notes = "Create account with provide account information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public AccountResource createAccount(@RequestBody AccountResource account) {
        return accountService.createAccount(account);
    }

    @PutMapping(path = "/{accountNumber}")
    @ApiOperation(value = "Update account", notes = "Update account with provide account information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public AccountResource updateAccount(@RequestBody AccountResource account, @PathVariable Long accountNumber) {
        account.setAccountNumber(accountNumber);
        return accountService.updateAccount(account);
    }

    @PostMapping(path = "/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Transfer account money", notes = "Transfer amount from one account to another")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    public void transfer(@Valid @RequestBody TransferResource transferResource) {
        if (Objects.equals(transferResource.getFromAccountNumber(), transferResource.getToAccountNumber())) {
            throw new IllegalArgumentException("Transfer accounts must be different");
        }

        accountService.transfer(transferResource);
    }
}
