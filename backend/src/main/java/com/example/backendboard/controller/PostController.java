package com.example.backendboard.controller;

import com.example.backendboard.dto.PostRequestDTO;
import com.example.backendboard.dto.PostResponseDTO;
import com.example.backendboard.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping
    public ResponseEntity<Long> createPost(
            @RequestPart("postRequest") PostRequestDTO postRequest,
            @RequestPart(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles) {
        // 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            throw new IllegalArgumentException("로그인 정보가 없습니다.");
        }

        String writerUsername = authentication.getName(); // 인증된 사용자 이름 가져오기
        Long postId = postService.createPost(postRequest, multipartFiles, writerUsername);
        return ResponseEntity.ok(postId);
    }

    /**
     * 게시글 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getPosts(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        List<PostResponseDTO> posts = postService.getPosts(page, size);
        return ResponseEntity.ok(posts);
    }

    /**
     * 게시글 총 개수 조회
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getPostCount(@RequestParam("type") String type) {
        Long postCount = postService.getPostCount(type);
        return ResponseEntity.ok(postCount);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long postId,
            @RequestPart("postRequest") PostRequestDTO postRequest,
            @RequestPart(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            throw new IllegalArgumentException("로그인 정보가 없습니다.");
        }

        String writerUsername = authentication.getName();
        postService.updatePost(postId, postRequest, multipartFiles, writerUsername);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPost(@PathVariable Long postId) {
        PostResponseDTO post = postService.getPost(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/search/{data}")
    public ResponseEntity<List<PostResponseDTO>> searchPosts(
            @PathVariable String data,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        List<PostResponseDTO> posts = postService.searchPosts(data, page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search/tags/{tag}")
    public ResponseEntity<List<PostResponseDTO>> searchPostsByTag(
            @PathVariable String tag,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        List<PostResponseDTO> posts = postService.searchPostsByTag(tag, page, size);
        return ResponseEntity.ok(posts);
    }
}

