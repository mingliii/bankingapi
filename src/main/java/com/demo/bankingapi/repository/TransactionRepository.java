package com.demo.bankingapi.repository;

import com.demo.bankingapi.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByAccount_AccountNumber(Long accountNumber, Pageable pageable);

    Page<Transaction> findAllByCreatedAtBetweenAndAccount_AccountNumber(LocalDateTime from, LocalDateTime to, Long accountNumber, Pageable pageable);
}