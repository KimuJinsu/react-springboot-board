package com.example.backendboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private String id;             // 사용자 이름 또는 ID
    private String options;        // 사용자 옵션
    private String profileImage;   // 사용자 프로필 이미지 URL
}