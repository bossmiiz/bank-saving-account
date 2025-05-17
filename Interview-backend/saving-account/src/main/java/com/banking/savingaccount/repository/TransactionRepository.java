package com.banking.savingaccount.repository;

import com.banking.savingaccount.model.Account;
import com.banking.savingaccount.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount(Account account);

    List<Transaction> findByAccountAndCreatedAtBetweenOrderByCreatedAtDesc(
            Account account, LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByTargetAccount(Account targetAccount);
}