package com.banking.savingaccount.dto;

import lombok.Data;

@Data
public class StatementRequest {
    private String date; // yyyy-MM-dd
    private String pin;
}