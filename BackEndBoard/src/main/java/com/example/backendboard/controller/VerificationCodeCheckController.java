package com.example.backendboard.controller;

import com.example.backendboard.dto.VerificationDTO;
import com.example.backendboard.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class VerificationCodeCheckController {

    private final VerificationCodeService verificationCodeService;

    @PostMapping("/mail/check")
    public ResponseEntity<String> checkVerificationCode(@RequestBody VerificationDTO verificationDTO) {
        boolean isValid = verificationCodeService.verifyCode(
                verificationDTO.getEmail(),
                verificationDTO.getCode()
        );
        if (isValid) {
            return ResponseEntity.ok("인증에 성공하였습니다.");
        }
        return ResponseEntity.badRequest().body("인증 코드가 유효하지 않습니다.");
    }
}