package com.example.backendboard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String email;
    private String password;
    private String checkPassword;
}