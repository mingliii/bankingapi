package com.demo.bankingapi.repository;

import com.demo.bankingapi.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByAccount_AccountNumberAndCreatedAtBetween(Long accountNumber, LocalDateTime from, LocalDateTime to, Pageable pageable);
}