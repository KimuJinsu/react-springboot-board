package com.example.backendboard.service;

import com.example.backendboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCheckService {

    private final UserRepository userRepository;

    public boolean isEmailOrUsernameTaken(String email, String username) {
        return userRepository.findByEmail(email).isPresent() ||
                userRepository.findByUsername(username).isPresent();
    }
}