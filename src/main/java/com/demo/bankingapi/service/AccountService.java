package com.demo.bankingapi.service;

import com.demo.bankingapi.domain.AccountResource;
import com.demo.bankingapi.entity.Account;
import com.demo.bankingapi.entity.Customer;
import com.demo.bankingapi.repository.AccountRepository;
import com.demo.bankingapi.repository.CustomerRepository;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Service
public class AccountService {

    private final ConversionService conversionService;

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    public AccountService(ConversionService conversionService,
                          AccountRepository accountRepository,
                          CustomerRepository customerRepository) {
        this.conversionService = conversionService;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    public AccountResource getByAccountNumber(Long accountNumber) {
        Account account =  accountRepository.getAccountByAccountNumber(accountNumber);
        return conversionService.convert(account, AccountResource.class);
    }

    public AccountResource createAccount(AccountResource accountResource) {
        Long customerNumber = accountResource.getCustomerNumber();
        Optional<Customer> customerOptional = customerRepository.findByCustomerNumber(customerNumber);

        if (customerOptional.isPresent() ) {
            Account account = conversionService.convert(accountResource, Account.class);
            Customer customer = customerOptional.get();
            customer.setAccounts(Collections.singletonList(account));
            customerRepository.save(customer);
        }

        throw new RuntimeException("Customer: " + customerNumber + " not found");
    }

    public AccountResource updateAccount(AccountResource accountResource) {
        Account account = conversionService.convert(accountResource, Account.class);
        account = accountRepository.save(requireNonNull(account));
        return conversionService.convert(account, AccountResource.class);
    }
}
