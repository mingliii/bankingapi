package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.domain.AccountResource;
import com.demo.bankingapi.entity.Account;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountResourceToAccountConverter implements Converter<AccountResource, Account> {

    @Override
    public Account convert(AccountResource accountResource) {
        return Account.builder()
                .accountNumber(accountResource.getAccountNumber())
                .balance(accountResource.getBalance())
                .status(accountResource.getStatus())
                .type(accountResource.getType())
                .build();

    }
}
