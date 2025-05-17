package com.banking.savingaccount.service;

import com.banking.savingaccount.dto.CreateAccountRequest;
import com.banking.savingaccount.dto.DepositRequest;
import com.banking.savingaccount.dto.TransferRequest;
import com.banking.savingaccount.model.Account;
import com.banking.savingaccount.model.User;

import java.util.List;

public interface AccountService {
    Account createAccount(Long userId, CreateAccountRequest request);

    Account findByAccountNumber(String accountNumber);

    List<Account> findByUser(User user);

    Account deposit(String accountNumber, DepositRequest request);

    Account transfer(String fromAccountNumber, TransferRequest request);

    List<Account> findByUserId(Long userId);

    boolean existsByAccountNumber(String accountNumber);
}