package com.example.backendboard.controller;

import com.example.backendboard.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VerificationCodeController {

    private final VerificationCodeService verificationCodeService;

    @PostMapping("/mail/send")
    public ResponseEntity<String> sendVerificationCode(@RequestBody Map<String, String> emailRequest) {
        String email = emailRequest.get("email");
        verificationCodeService.generateAndSendCode(email);
        return ResponseEntity.ok("인증번호가 전송되었습니다.");
    }
}