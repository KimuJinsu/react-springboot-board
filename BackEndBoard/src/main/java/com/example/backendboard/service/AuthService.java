package com.example.backendboard.service;

import com.example.backendboard.dto.LoginRequestDTO;
import com.example.backendboard.dto.TokenResponseDTO;
import com.example.backendboard.entity.RefreshToken;
import com.example.backendboard.entity.User;
import com.example.backendboard.repository.RefreshTokenRepository;
import com.example.backendboard.repository.UserRepository;
import com.example.backendboard.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public TokenResponseDTO login(LoginRequestDTO request) {
        // 사용자 확인
        User user = userRepository.findByUsername(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        // RefreshToken 저장
        refreshTokenRepository.save(new RefreshToken(refreshToken, user.getUsername(),
                LocalDateTime.now().plusDays(7)));

        return new TokenResponseDTO(accessToken, refreshToken);
    }
}