package com.example.backendboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostResponseDTO {
    private Long id;
    private int number; // 번호
    private String title;
    private String content;
    private String writer;
    private List<String> attachments;
}