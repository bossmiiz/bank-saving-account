package com.banking.savingaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatementDto {
    private String date; // yyyy-MM-dd
    private String time; // HH:mm
    private String code; // เช่น A0, A1, A3
    private String channel; // OTC, ATS
    private String debitCredit; // +100.00 หรือ -50.00
    private BigDecimal balance;
    private String remark;
}