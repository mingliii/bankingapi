package com.demo.bankingapi.repository;

import com.demo.bankingapi.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountNumber(Long accountNumber);
    List<Account> findByCustomer_CustomerNumber(Long customerNumber);
}