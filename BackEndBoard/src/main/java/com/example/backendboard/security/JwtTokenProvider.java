package com.example.backendboard.security;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key secretKey = new SecretKeySpec(
            "your-secret-key-your-secret-key!".getBytes(),
            SignatureAlgorithm.HS256.getJcaName()
    );

    private final long accessTokenValidity = 30 * 60 * 1000L; // 30분
    private final long refreshTokenValidity = 7 * 24 * 60 * 60 * 1000L; // 7일

    // Access Token 생성
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰에서 사용자 이름 추출
    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("추출된 사용자 이름: " + claims.getSubject());
            return claims.getSubject();
        } catch (JwtException e) {
            System.out.println("사용자 이름 추출 실패: " + e.getMessage());
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            System.out.println("검증할 토큰: " + token);
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            System.out.println("토큰 검증 성공");
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("토큰 만료: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 토큰: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("손상된 토큰: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("서명 검증 실패: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("토큰이 null이거나 빈 값입니다: " + e.getMessage());
        }
        return false;
    }
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }

    // HTTP 요청에서 Authorization 헤더로부터 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        System.out.println("받은 Authorization 헤더: " + bearerToken); // 디버깅 로그 추가
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Authentication 객체 생성 (추가된 메서드)
    public Authentication getAuthentication(String username) {
        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}