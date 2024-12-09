package com.example.backendboard.controller;

import com.example.backendboard.dto.LoginRequestDTO;
import com.example.backendboard.dto.TokenResponseDTO;
import com.example.backendboard.entity.RefreshToken;
import com.example.backendboard.repository.RefreshTokenRepository;
import com.example.backendboard.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @PostMapping("/logins")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        // 로그인 처리 및 토큰 발급
        TokenResponseDTO tokens = authService.login(request);

        // 액세스 토큰은 Authorization 헤더에 포함
        response.setHeader("Authorization", "Bearer " + tokens.getAccessToken());

        // 기존의 Refresh Token이 존재하면 삭제
        String username = request.getId();
        refreshTokenRepository.deleteByUsername(username);

        // 새로운 Refresh Token 생성 및 저장
        RefreshToken refreshToken = new RefreshToken(tokens.getRefreshToken(), username, LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshToken);


        // 리프레시 토큰은 쿠키로 전달
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshTokenCookie);



        return ResponseEntity.ok("로그인 성공");
    }
}