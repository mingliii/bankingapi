package com.demo.bankingapi.repository;

import com.demo.bankingapi.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByAccount_AccountNumber(Long accountNumber, Pageable pageable);
}