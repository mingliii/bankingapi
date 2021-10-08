package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.domain.AccountResource;
import com.demo.bankingapi.entity.Account;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountToAccountResourceConverter implements Converter<Account, AccountResource> {

    @Override
    public AccountResource convert(Account account) {
        return AccountResource.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .status(account.getStatus())
                .type(account.getType())
                .build();

    }
}
