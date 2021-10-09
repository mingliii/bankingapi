package com.demo.bankingapi.service;

import com.demo.bankingapi.domain.AccountResource;
import com.demo.bankingapi.domain.TransferResource;
import com.demo.bankingapi.entity.Account;
import com.demo.bankingapi.entity.Currency;
import com.demo.bankingapi.entity.Customer;
import com.demo.bankingapi.repository.AccountRepository;
import com.demo.bankingapi.repository.CustomerRepository;
import com.demo.bankingapi.service.exception.InsufficientBalanceException;
import com.demo.bankingapi.service.exception.NotFoundException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

// todo test
@Service
public class AccountService {

    private final ConversionService conversionService;

    private final TransactionService transactionService;

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    public AccountService(ConversionService conversionService,
                          TransactionService transactionService,
                          AccountRepository accountRepository,
                          CustomerRepository customerRepository) {
        this.transactionService = transactionService;
        this.conversionService = conversionService;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    public List<AccountResource> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(account -> conversionService.convert(account, AccountResource.class))
                .collect(Collectors.toList());
    }

    public AccountResource getByAccountNumber(Long accountNumber) {
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);
        if (accountOptional.isPresent()) {
            return conversionService.convert(accountOptional.get(), AccountResource.class);
        }

        throw new NotFoundException(Account.class, accountNumber);
    }

    public AccountResource createAccount(AccountResource accountResource) {
        if (accountResource.getCustomerNumber() == null) {
            throw new IllegalArgumentException("Customer number must be provided");
        }

        Long customerNumber = accountResource.getCustomerNumber();
        Optional<Customer> customerOptional = customerRepository.findByCustomerNumber(customerNumber);

        if (customerOptional.isPresent()) {
            Account account = conversionService.convert(accountResource, Account.class);
            account = accountRepository.save(requireNonNull(account));
            Customer customer = customerOptional.get();
            customer.addAccount(requireNonNull(account));
            customerRepository.save(customer);
            return conversionService.convert(account, AccountResource.class);
        }

        throw new NotFoundException(Customer.class, customerNumber);
    }

    @Transactional
    public AccountResource updateAccount(AccountResource accountResource) {
        Account account = conversionService.convert(accountResource, Account.class);
        account = accountRepository.save(requireNonNull(account));
        return conversionService.convert(account, AccountResource.class);
    }

    @Transactional
    public void transfer(TransferResource transferResource) {
        Optional<Account> fromOptional = accountRepository.findByAccountNumber(transferResource.getFromAccountNumber());
        Optional<Account> toOptional = accountRepository.findByAccountNumber(transferResource.getToAccountNumber());

        if (fromOptional.isEmpty()) {
            throw new NotFoundException(Account.class, transferResource.getFromAccountNumber());
        }

        if (toOptional.isEmpty()) {
            throw new NotFoundException(Account.class, transferResource.getToAccountNumber());
        }

        Account from = fromOptional.get();
        Account to = toOptional.get();

        if (from.getBalance().compareTo(transferResource.getAmount()) < 0) {
            throw new InsufficientBalanceException(from);
        }

        from.setBalance(from.getBalance().subtract(transferResource.getAmount()));
        to.setBalance(to.getBalance().add(transferResource.getAmount()));

        Currency currency = Currency.from(transferResource.getCurrency());
        transactionService.createTransactions(from, to, transferResource.getAmount(), currency);

        accountRepository.save(from);
        accountRepository.save(to);
    }
}
