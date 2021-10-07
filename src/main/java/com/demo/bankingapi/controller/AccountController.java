package com.demo.bankingapi.controller;

import com.demo.bankingapi.domain.AccountResource;
import com.demo.bankingapi.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public void addAccount(@RequestBody AccountResource account) {

        // todo
    }


}
