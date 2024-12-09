package com.example.backendboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    private String token;

    private String username;


    private LocalDateTime expiration;

    public RefreshToken(String token, String username, LocalDateTime expiration) {
        this.token = token;
        this.username = username;
        this.expiration = expiration;
    }
}