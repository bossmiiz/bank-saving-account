package com.banking.savingaccount.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String citizenId;
    private String thaiName;
    private String englishName;
    private String pin;
}