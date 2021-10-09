package com.demo.bankingapi.service.exception;

import com.demo.bankingapi.entity.Account;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// todo create customised error message
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(Account account) {
        super(String.format("Account: %s - insufficient amount", account.getAccountNumber()));
    }
}
