package com.example.backendboard.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String id;
    private String password;
}