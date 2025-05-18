package com.banking.savingaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatementDto {
    private String date;
    private String time;
    private String code;
    private String channel;
    private String debitCredit;
    private BigDecimal balance;
    private String remark;
}