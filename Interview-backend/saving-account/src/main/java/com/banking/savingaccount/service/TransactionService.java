package com.banking.savingaccount.service;

import com.banking.savingaccount.dto.StatementRequest;
import com.banking.savingaccount.dto.TransactionStatementDto;
import com.banking.savingaccount.model.Account;
import com.banking.savingaccount.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    List<Transaction> getStatement(String accountNumber, StatementRequest request);

    List<Transaction> getAccountTransactions(Account account);

    List<Transaction> getAccountTransactions(String accountNumber);

    List<Transaction> getAccountTransactionsByDateRange(Account account, LocalDateTime startDate,
            LocalDateTime endDate);

    List<Transaction> getAccountTransactionsByMonth(Account account, int year, int month);

    List<Transaction> getIncomingTransfers(Account account);

    List<TransactionStatementDto> getStatementByDate(String accountNumber, StatementRequest request);

    List<TransactionStatementDto> getAccountTransactionsDto(String accountNumber);
}