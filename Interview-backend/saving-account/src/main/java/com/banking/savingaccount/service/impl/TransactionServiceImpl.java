package com.banking.savingaccount.service.impl;

import com.banking.savingaccount.dto.StatementRequest;
import com.banking.savingaccount.model.Account;
import com.banking.savingaccount.model.Transaction;
import com.banking.savingaccount.repository.AccountRepository;
import com.banking.savingaccount.repository.TransactionRepository;
import com.banking.savingaccount.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.banking.savingaccount.dto.TransactionStatementDto;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getStatement(String accountNumber, StatementRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getPin().equals(request.getPin())) {
            throw new RuntimeException("Invalid PIN");
        }

        // ไม่ต้องใช้ year/month แล้ว (deprecated)
        return new ArrayList<>();
    }

    @Override
    public List<Transaction> getAccountTransactions(Account account) {
        return transactionRepository.findByAccount(account);
    }

    @Override
    public List<Transaction> getAccountTransactionsByDateRange(Account account, LocalDateTime startDate,
            LocalDateTime endDate) {
        return transactionRepository.findByAccountAndCreatedAtBetweenOrderByCreatedAtDesc(account, startDate, endDate);
    }

    @Override
    public List<Transaction> getAccountTransactionsByMonth(Account account, int year, int month) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
        return getAccountTransactionsByDateRange(account, startDate, endDate);
    }

    @Override
    public List<Transaction> getIncomingTransfers(Account account) {
        return transactionRepository.findByTargetAccount(account);
    }

    @Override
    public List<Transaction> getAccountTransactions(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return getAccountTransactions(account);
    }

    @Override
    public List<TransactionStatementDto> getStatementByDate(String accountNumber, StatementRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (!account.getUser().getPin().equals(request.getPin())) {
            throw new RuntimeException("Invalid PIN");
        }
        // filter เฉพาะวันเดียว
        LocalDateTime startDate = LocalDateTime.parse(request.getDate() + "T00:00:00");
        LocalDateTime endDate = startDate.plusDays(1).minusSeconds(1);
        List<Transaction> txs = transactionRepository.findByAccountAndCreatedAtBetweenOrderByCreatedAtDesc(account,
                startDate, endDate);
        return mapToStatementDto(txs, account);
    }

    @Override
    public List<TransactionStatementDto> getAccountTransactionsDto(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        List<Transaction> txs = transactionRepository.findByAccount(account);
        return mapToStatementDto(txs, account);
    }

    private List<TransactionStatementDto> mapToStatementDto(List<Transaction> txs, Account account) {
        List<TransactionStatementDto> result = new ArrayList<>();
        BigDecimal runningBalance = account.getBalance();
        // เรียงจากอดีต -> ปัจจุบัน
        txs.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        for (Transaction tx : txs) {
            String code = mapTypeToCode(tx.getType());
            String channel = mapTypeToChannel(tx.getType());
            String remark = mapRemark(tx);
            String debitCredit;
            if (tx.getType().name().equals("DEPOSIT")) {
                debitCredit = "+" + tx.getAmount().toString();
                runningBalance = runningBalance.subtract(tx.getAmount());
            } else {
                debitCredit = "-" + tx.getAmount().toString();
                runningBalance = runningBalance.add(tx.getAmount());
            }
            result.add(new TransactionStatementDto(
                    tx.getCreatedAt().toLocalDate().toString(),
                    tx.getCreatedAt().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                    code,
                    channel,
                    debitCredit,
                    runningBalance,
                    remark));
        }
        return result;
    }

    private String mapTypeToCode(com.banking.savingaccount.model.TransactionType type) {
        switch (type) {
            case DEPOSIT:
                return "A0";
            case WITHDRAWAL:
                return "A1";
            case TRANSFER:
                return "A3";
            default:
                return "A9";
        }
    }

    private String mapTypeToChannel(com.banking.savingaccount.model.TransactionType type) {
        switch (type) {
            case DEPOSIT:
            case WITHDRAWAL:
                return "OTC";
            case TRANSFER:
                return "ATS";
            default:
                return "OTC";
        }
    }

    private String mapRemark(Transaction tx) {
        switch (tx.getType()) {
            case DEPOSIT:
                return "Deposit Terminal";
            case WITHDRAWAL:
                return "Withdraw Terminal";
            case TRANSFER:
                if (tx.getTargetAccount() != null) {
                    return "Transfer to x" + tx.getTargetAccount().getAccountNumber().substring(3) + " "
                            + tx.getTargetAccount().getUser().getEnglishName();
                } else {
                    return "Transfer";
                }
            default:
                return "-";
        }
    }
}