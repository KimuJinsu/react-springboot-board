package com.example.backendboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDTO {
    private String title;
    private String content;
    private boolean blockComment;
    private boolean isPrivate;
    private List<String> tags; // 태그 필드 추가

}