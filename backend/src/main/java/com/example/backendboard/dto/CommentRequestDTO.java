package com.example.backendboard.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDTO {
    private Long postId; // 게시글 ID
    private String content; // 댓글 내용
}