package com.demo.bankingapi.service.conveter;

import com.demo.bankingapi.resource.AccountResource;
import com.demo.bankingapi.entity.Account;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


/**
 * A convert that converts {@link Account} entity objects to {@link AccountResource} resource objects.
 */
@Component
public class AccountToAccountResourceConverter implements Converter<Account, AccountResource> {

    @Override
    public AccountResource convert(Account account) {
        return AccountResource.builder()
                .accountNumber(account.getAccountNumber())
                .customerNumber(account.getCustomer().getCustomerNumber())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .status(account.getStatus() != null ? account.getStatus().name() : null)
                .type(account.getType() != null ? account.getType().name() : null)
                .build();
    }
}
