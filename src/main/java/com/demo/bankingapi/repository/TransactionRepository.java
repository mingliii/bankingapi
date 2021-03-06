package com.demo.bankingapi.repository;

import com.demo.bankingapi.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByAccount_AccountNumber(Long accountNumber);
    Page<Transaction> findAllByAccount_AccountNumberAndCreatedAtBetween(Long accountNumber, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<Transaction> findAllByAccount_AccountNumberAndTypeAndCreatedAtBetween(Long accountNumber, Transaction.Type type, LocalDateTime from, LocalDateTime to, Pageable pageable);
}