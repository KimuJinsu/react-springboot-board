package com.example.backendboard.controller;

import com.example.backendboard.service.UserCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserCheckController {

    private final UserCheckService userCheckService;

    @GetMapping("/users/{email}/{username}")
    public ResponseEntity<String> checkEmailAndUsername(
            @PathVariable String email,
            @PathVariable String username) {
        if (userCheckService.isEmailOrUsernameTaken(email, username)) {
            return ResponseEntity.badRequest().body("이미 사용 중인 이메일 또는 아이디입니다.");
        }
        return ResponseEntity.ok("사용 가능한 이메일 및 아이디입니다.");
    }
}