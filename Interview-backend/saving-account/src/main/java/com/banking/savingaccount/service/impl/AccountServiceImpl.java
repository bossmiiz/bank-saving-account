package com.banking.savingaccount.service.impl;

import com.banking.savingaccount.dto.CreateAccountRequest;
import com.banking.savingaccount.dto.DepositRequest;
import com.banking.savingaccount.dto.TransferRequest;
import com.banking.savingaccount.model.Account;
import com.banking.savingaccount.model.Transaction;
import com.banking.savingaccount.model.TransactionType;
import com.banking.savingaccount.model.User;
import com.banking.savingaccount.repository.AccountRepository;
import com.banking.savingaccount.repository.TransactionRepository;
import com.banking.savingaccount.repository.UserRepository;
import com.banking.savingaccount.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Account createAccount(Long userId, CreateAccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accountNumber = generateAccountNumber();
        while (accountRepository.existsByAccountNumber(accountNumber)) {
            accountNumber = generateAccountNumber();
        }

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(request.getInitialDeposit());
        account.setUser(user);

        account = accountRepository.save(account);

        if (request.getInitialDeposit().compareTo(BigDecimal.ZERO) > 0) {
            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setType(TransactionType.DEPOSIT);
            transaction.setAmount(request.getInitialDeposit());
            transactionRepository.save(transaction);
        }

        return account;
    }

    @Override
    @Transactional
    public Account deposit(String accountNumber, DepositRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(request.getAmount()));
        account = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transactionRepository.save(transaction);

        return account;
    }

    @Override
    @Transactional
    public Account transfer(String fromAccountNumber, TransferRequest request) {
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("From account not found"));

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("To account not found"));

        if (!fromAccount.getUser().getPin().equals(request.getPin())) {
            throw new RuntimeException("Invalid PIN");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        fromAccount = accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setAccount(fromAccount);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAmount(request.getAmount());
        transaction.setTargetAccount(toAccount);
        transactionRepository.save(transaction);

        return fromAccount;
    }

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public List<Account> findByUser(User user) {
        return accountRepository.findByUser(user);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public List<Account> findByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return findByUser(user);
    }
}