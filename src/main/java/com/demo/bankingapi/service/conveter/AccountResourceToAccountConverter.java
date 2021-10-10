package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.resource.AccountResource;
import com.demo.bankingapi.entity.Account;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * A convert that converts {@link AccountResource} resource objects to {@link Account} entity objects.
 */
@Component
public class AccountResourceToAccountConverter implements Converter<AccountResource, Account> {

    @Override
    public Account convert(AccountResource accountResource) {
        return Account.builder()
                .accountNumber(accountResource.getAccountNumber())
                .balance(accountResource.getBalance())
                .status(Account.Status.from(accountResource.getStatus()))
                .type(Account.Type.from(accountResource.getType()))
                .build();
    }
}
