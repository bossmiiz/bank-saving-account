package com.banking.savingaccount.controller;

import com.banking.savingaccount.dto.CreateAccountRequest;
import com.banking.savingaccount.dto.DepositRequest;
import com.banking.savingaccount.dto.TransferRequest;
import com.banking.savingaccount.model.Account;
import com.banking.savingaccount.model.User;
import com.banking.savingaccount.model.UserRole;
import com.banking.savingaccount.service.AccountService;
import com.banking.savingaccount.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Account> createAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateAccountRequest request) {
        User teller = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!teller.getRoles().contains(UserRole.ROLE_TELLER)) {
            throw new RuntimeException("Only TELLER can create accounts");
        }

        validateCustomerInfo(request);

        User customer = userService.findByCitizenId(request.getCitizenId())
                .orElseGet(() -> userService.createUserByTeller(
                        request.getCitizenId(),
                        request.getThaiName(),
                        request.getEnglishName()));

        if (!customer.getRoles().contains(UserRole.ROLE_CUSTOMER)) {
            customer.getRoles().add(UserRole.ROLE_CUSTOMER);
            customer = userService.save(customer);
        }

        Account account = accountService.createAccount(customer.getId(), request);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<Account> deposit(
            @PathVariable String accountNumber,
            @RequestBody DepositRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User teller = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!teller.getRoles().contains(UserRole.ROLE_TELLER)) {
            throw new RuntimeException("Only TELLER can deposit money");
        }

        Account account = accountService.deposit(accountNumber, request);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{accountNumber}/transfer")
    public ResponseEntity<Account> transfer(
            @PathVariable String accountNumber,
            @RequestBody TransferRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User customer = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!customer.getRoles().contains(UserRole.ROLE_CUSTOMER)) {
            throw new RuntimeException("Only CUSTOMER can transfer money");
        }

        if (!customer.getPin().equals(request.getPin())) {
            throw new RuntimeException("Invalid PIN");
        }

        Account account = accountService.transfer(accountNumber, request);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        Account account = accountService.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Account> accounts = accountService.findByUser(user);
        return ResponseEntity.ok(accounts);
    }

    private void validateCustomerInfo(CreateAccountRequest request) {
        if (request.getCitizenId() == null || request.getThaiName() == null
                || request.getEnglishName() == null) {
            throw new RuntimeException("กรุณากรอกข้อมูลลูกค้าให้ครบถ้วน (citizenId, thaiName, englishName)");
        }
    }
}