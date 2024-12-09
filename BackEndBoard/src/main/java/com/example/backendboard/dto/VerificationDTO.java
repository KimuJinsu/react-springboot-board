package com.example.backendboard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VerificationDTO {
    private String email;
    private String code;
}