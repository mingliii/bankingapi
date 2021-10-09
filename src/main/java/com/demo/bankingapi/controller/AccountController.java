package com.demo.bankingapi.controller;

import com.demo.bankingapi.domain.AccountResource;
import com.demo.bankingapi.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/{accountNumber}")
    public AccountResource getByAccountNumber(@PathVariable Long accountNumber) {

        return null;
    }

    @PostMapping(path = "/add/{customerNumber}")
    public void createAccount(@RequestBody AccountResource account) {

        // todo
    }


}
