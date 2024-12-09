package com.example.backendboard.service;

import com.example.backendboard.entity.VerificationCode;
import com.example.backendboard.repository.VerificationCodeRepository;
import com.example.backendboard.smtp.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final MailService mailService;

    @Transactional
    public void generateAndSendCode(String email) {
        validateEmail(email); // 이메일 형식 검증

        // 인증 코드 생성
        String code = UUID.randomUUID().toString().substring(0, 6);

        // 기존 이메일 여부 확인 및 데이터 업데이트 또는 삽입
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email)
                .orElse(new VerificationCode(email, code, LocalDateTime.now().plusMinutes(10)));

        verificationCode.setCode(code);
        verificationCode.setExpirationTime(LocalDateTime.now().plusMinutes(10));
        verificationCodeRepository.save(verificationCode);

        // 이메일 전송 (트랜잭션 외부에서 처리)
        sendVerificationCode(email, code);
    }

    public boolean verifyCode(String email, String code) {
        // 이메일로 인증 코드 조회
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("인증 코드를 찾을 수 없습니다."));

        // 인증 코드와 만료 시간 확인
        return verificationCode.getCode().equals(code) &&
                verificationCode.getExpirationTime().isAfter(LocalDateTime.now());
    }

    private void sendVerificationCode(String email, String code) {
        try {
            mailService.sendVerificationCode(email, code);
            log.info("인증 코드가 성공적으로 전송되었습니다: {}", email);
        } catch (Exception e) {
            log.error("이메일 전송 중 오류가 발생했습니다: {}", email, e);
            throw new RuntimeException("이메일 전송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            throw new IllegalArgumentException("잘못된 이메일 형식입니다: " + email);
        }
    }
}