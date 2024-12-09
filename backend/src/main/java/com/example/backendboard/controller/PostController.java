package com.example.backendboard.controller;

import com.example.backendboard.dto.CountResponseDTO;
import com.example.backendboard.dto.PostRequestDTO;
import com.example.backendboard.dto.PostResponseDTO;
import com.example.backendboard.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestPart("postRequest") PostRequestDTO postRequest,
                                        @RequestPart(value = "multipartFiles", required = false) List<MultipartFile> files,
                                        Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        String username = authentication.getName();
        Long postId = postService.createPost(postRequest, files, username);
        return ResponseEntity.ok(postId);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                        @RequestPart("postRequest") PostRequestDTO postRequest,
                                        @RequestPart(value = "multipartFiles", required = false) List<MultipartFile> files,
                                        Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        String username = authentication.getName();
        System.out.println("인증된 사용자: " + authentication.getName()); // 사용자 정보 확인 로그

        try {
            // 요청 데이터 확인 (디버깅용)
            System.out.println("Post ID: " + postId);
            System.out.println("Request: " + postRequest);

            postService.updatePost(postId, postRequest, files, username);
            return ResponseEntity.ok("게시글이 수정되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }


    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getPosts(@RequestParam int page, @RequestParam int size) {
        List<PostResponseDTO> posts = postService.getPosts(page, size);
        return ResponseEntity.ok(posts);
    }

    // 키워드 검색
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<PostResponseDTO>> searchPosts(@PathVariable String keyword,
                                                             @RequestParam int page,
                                                             @RequestParam int size) {
        List<PostResponseDTO> posts = postService.searchPosts(keyword, page, size);
        return ResponseEntity.ok(posts);
    }

    // 태그 검색
    @GetMapping("/search/tags/{tag}")
    public ResponseEntity<List<PostResponseDTO>> searchByTag(@PathVariable String tag,
                                                             @RequestParam int page,
                                                             @RequestParam int size) {
        List<PostResponseDTO> posts = postService.searchPostsByTag(tag, page, size);
        return ResponseEntity.ok(posts);
    }

    // 게시글 개수 조회
    @GetMapping("/count")
    public ResponseEntity<CountResponseDTO> getPostCount(@RequestParam String type,
                                                         @RequestParam(required = false) String data) {
        Long count = postService.getPostCount(type, data);
        return ResponseEntity.ok(new CountResponseDTO(count));
    }

    @PatchMapping("/views/{id}")
    public ResponseEntity<?> increaseViews(@PathVariable Long id) {
        postService.increaseViewCount(id);
        return ResponseEntity.ok("조회수가 증가했습니다.");
    }

    @PatchMapping("/likes/{id}")
    public ResponseEntity<?> likePost(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        String username = authentication.getName();
        try {
            postService.likePost(id, username);
            return ResponseEntity.ok("좋아요 처리가 완료되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id, Authentication authentication) {
        String currentUsername = authentication != null && authentication.isAuthenticated() ? authentication.getName() : null;
        try {
            PostResponseDTO post = postService.getPost(id, currentUsername);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        String currentUsername = authentication.getName();
        try {
            postService.deletePost(id, currentUsername);
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
