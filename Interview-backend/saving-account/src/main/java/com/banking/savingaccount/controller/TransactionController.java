package com.banking.savingaccount.controller;

import com.banking.savingaccount.dto.StatementRequest;
import com.banking.savingaccount.dto.TransactionStatementDto;
import com.banking.savingaccount.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/{accountNumber}/statement")
    public ResponseEntity<List<TransactionStatementDto>> getStatement(
            @PathVariable String accountNumber,
            @RequestBody StatementRequest request) {
        List<TransactionStatementDto> statement = transactionService.getStatementByDate(accountNumber, request);
        return ResponseEntity.ok(statement);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<List<TransactionStatementDto>> getAccountTransactions(
            @PathVariable String accountNumber) {
        List<TransactionStatementDto> transactions = transactionService.getAccountTransactionsDto(accountNumber);
        return ResponseEntity.ok(transactions);
    }
}