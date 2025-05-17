package com.banking.savingaccount.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    private String toAccountNumber;
    private BigDecimal amount;
    private String pin;
}