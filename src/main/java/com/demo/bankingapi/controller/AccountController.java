package com.demo.bankingapi.controller;

import com.demo.bankingapi.domain.AccountResource;
import com.demo.bankingapi.domain.TransactionResource;
import com.demo.bankingapi.domain.TransferResource;
import com.demo.bankingapi.service.AccountService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size,
                                               @RequestParam(defaultValue = "true") boolean desc
                                                     ) {
        return accountService.getTransactions(accountNumber, page, size, desc);
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
        accountService.transfer(transferResource);
    }
}
