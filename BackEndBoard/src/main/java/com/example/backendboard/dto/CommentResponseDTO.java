package com.example.backendboard.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private String content;
    private String username; // 작성자
    private String profileImageUrl; // 추가된 필드
    private String createdAt;

    public CommentResponseDTO(Long id, String content, String username, String profileImageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
