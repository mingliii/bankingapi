package com.demo.bankingapi.service;

import com.demo.bankingapi.entity.Account;
import com.demo.bankingapi.entity.Currency;
import com.demo.bankingapi.entity.Customer;
import com.demo.bankingapi.entity.Transaction;
import com.demo.bankingapi.repository.AccountRepository;
import com.demo.bankingapi.repository.CustomerRepository;
import com.demo.bankingapi.resource.AccountResource;
import com.demo.bankingapi.resource.TransactionResource;
import com.demo.bankingapi.resource.TransferResource;
import com.demo.bankingapi.service.exception.InsufficientBalanceException;
import com.demo.bankingapi.service.exception.NotFoundException;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

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

    @Transactional(readOnly = true)
    public List<AccountResource> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(account -> conversionService.convert(account, AccountResource.class))
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public AccountResource getByAccountNumber(Long accountNumber) {
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);
        if (accountOptional.isPresent()) {
            return conversionService.convert(accountOptional.get(), AccountResource.class);
        }

        throw new NotFoundException(Account.class, accountNumber);
    }

    @Transactional
    public AccountResource createAccount(AccountResource accountResource) {
        if (accountResource.getCustomerNumber() == null) {
            throw new IllegalArgumentException("Customer number must be provided");
        }

        Long customerNumber = accountResource.getCustomerNumber();
        Optional<Customer> customerOptional = customerRepository.findByCustomerNumber(customerNumber);

        if (customerOptional.isPresent()) {
            Account account = conversionService.convert(accountResource, Account.class);
            Customer customer = customerOptional.get();
            customer.addAccount(requireNonNull(account));
            accountRepository.save(account);
            Currency currency = accountResource.getCurrency() != null ? accountResource.getCurrency(): Currency.GBP;

            if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                transactionService.createDepositTransactions(account, accountResource.getBalance(), currency);
            }

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
        if (fromOptional.isEmpty()) {
            throw new NotFoundException(Account.class, transferResource.getFromAccountNumber());
        }

        Optional<Account> toOptional = accountRepository.findByAccountNumber(transferResource.getToAccountNumber());
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
        transactionService.createTransferTransactions(from, to, transferResource.getAmount(), currency);

        accountRepository.save(from);
        accountRepository.save(to);
    }

    public List<TransactionResource> getTransactions(Long accountNumber, String type, LocalDateTime from, LocalDateTime to, int page, int size, boolean descending) {
        Sort sortBy = Sort.by("createdAt");
        if (descending) {
            sortBy = sortBy.descending();
        }

        return transactionService.getTransactions(accountNumber, Transaction.Type.from(type), from, to, PageRequest.of(page, size, sortBy));
    }
}
