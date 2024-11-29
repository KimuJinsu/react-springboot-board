package com.example.backendboard.controller;

import com.example.backendboard.dto.UserResponseDTO;
import com.example.backendboard.entity.User;
import com.example.backendboard.repository.UserRepository;
import com.example.backendboard.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ResponseEntity<UserResponseDTO> getUserInfo(HttpServletRequest request) {
        // 헤더에서 Access Token 추출
        String token = jwtTokenProvider.resolveToken(request);
        System.out.println("받은 Authorization 헤더: " + request.getHeader("Authorization")); // 디버깅용 로그
        System.out.println("추출한 Access Token: " + token); // 디버깅용 로그

        // 토큰 검증
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            System.err.println("유효하지 않은 토큰입니다."); // 디버깅용 로그
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 토큰에서 username 추출
        String username = jwtTokenProvider.extractUsername(token);
        System.out.println("추출된 사용자 이름: " + username); // 디버깅용 로그

        // username으로 User 엔티티 검색
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.err.println("사용자를 찾을 수 없습니다: " + username); // 디버깅용 로그
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
                });
        //user.getUsername()
        // 응답 데이터 생성
        UserResponseDTO response = new UserResponseDTO(
                user.getUsername(),          // username
                "default",                   // 옵션 (기본값 설정 가능)
                user.getProfileImageUrl()    // 프로필 이미지
        );
        System.out.println("응답 데이터: " + response); // 디버깅용 로그

        return ResponseEntity.ok(response);
    }

}