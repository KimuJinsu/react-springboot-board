package com.example.backendboard.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDTO {
    private String title;
    private String content;
    private boolean privatePost;
    private boolean blockComment;
    private List<String> tags;
    private List<Long> deletedFileIds;
}
