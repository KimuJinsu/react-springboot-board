package com.example.backendboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class PostResponseDTO {
    private Long id;
    private String title;
    private String content;
    private boolean privatePost;
    private boolean blockComment;
    private List<String> tags;
    private List<AttachmentResponseDTO> attachments;
    private String writerUsername; // 작성자 이름
    private String writerProfileImageUrl; // 작성자 프로필 이미지
    private LocalDateTime postDate; // 작성일 추가
    private int likes;    // 추가된 필드
    private int views;    // 추가된 필드
}