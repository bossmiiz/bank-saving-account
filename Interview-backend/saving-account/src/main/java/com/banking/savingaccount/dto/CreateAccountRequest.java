package com.banking.savingaccount.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    private String citizenId;
    private String thaiName;
    private String englishName;
    private BigDecimal initialDeposit;
}