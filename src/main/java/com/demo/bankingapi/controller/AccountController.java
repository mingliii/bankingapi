package com.demo.bankingapi.controller;

import com.demo.bankingapi.resource.AccountResource;
import com.demo.bankingapi.resource.TransactionResource;
import com.demo.bankingapi.resource.TransferResource;
import com.demo.bankingapi.service.AccountService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResource> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping(path = "/{accountNumber}")
    public AccountResource getByAccountNumber(@PathVariable Long accountNumber) {
        return accountService.getByAccountNumber(accountNumber);
    }

    @GetMapping(path = "/{accountNumber}/transactions")
    public List<TransactionResource> getTransactions(@PathVariable Long accountNumber,
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                     @RequestParam(required = false, defaultValue = "1970-01-01") LocalDate from,
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                     @RequestParam(required = false, defaultValue = "2099-12-31") LocalDate to,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size,
                                                     @RequestParam(defaultValue = "true") boolean desc
    ) {

        return accountService.getTransactions(accountNumber, from.atStartOfDay(), to.plusDays(1).atStartOfDay(), page, size, desc);
    }

    @PostMapping
    public AccountResource createAccount(@RequestBody AccountResource account) {
        return accountService.createAccount(account);
    }

    @PutMapping(path = "/{accountNumber}")
    public AccountResource updateAccount(@RequestBody AccountResource account, @PathVariable Long accountNumber) {
        account.setAccountNumber(accountNumber);
        return accountService.updateAccount(account);
    }

    @PostMapping(path = "/transfer")
    public void transfer(@Valid @RequestBody TransferResource transferResource) {
        if (Objects.equals(transferResource.getFromAccountNumber(), transferResource.getToAccountNumber())) {
            throw new IllegalArgumentException("Transfer accounts must be different");
        }

        accountService.transfer(transferResource);
    }
}
