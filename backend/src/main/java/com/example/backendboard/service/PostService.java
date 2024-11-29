package com.example.backendboard.service;

import com.example.backendboard.dto.PostRequestDTO;
import com.example.backendboard.dto.PostResponseDTO;
import com.example.backendboard.entity.Post;
import com.example.backendboard.entity.Tag;
import com.example.backendboard.entity.User;
import com.example.backendboard.repository.PostRepository;
import com.example.backendboard.repository.UserRepository;
import com.example.backendboard.s3.S3Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public Long createPost(PostRequestDTO postRequest, List<MultipartFile> multipartFiles, String writerUsername) {
        User writer = userRepository.findByUsername(writerUsername)
                .orElseThrow(() -> new IllegalArgumentException("작성자를 찾을 수 없습니다."));

        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setBlockComment(postRequest.isBlockComment());
        post.setPrivate(postRequest.isPrivate());
        post.setWriter(writer);

        // Tag 추가
        List<Tag> tags = postRequest.getTags().stream()
                .map(tagName -> {
                    Tag tag = new Tag();
                    tag.setName(tagName);
                    tag.setPost(post);
                    return tag;
                })
                .collect(Collectors.toList());
        post.setTags(tags);

        // 파일 업로드 처리
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            List<String> fileUrls = multipartFiles.stream()
                    .map(file -> {
                        try {
                            return s3Service.uploadFile(file);
                        } catch (IOException e) {
                            throw new RuntimeException("파일 업로드 실패: " + file.getOriginalFilename(), e);
                        }
                    })
                    .collect(Collectors.toList());
            post.setAttachments(fileUrls);
        }

        return postRepository.save(post).getId();
    }
//    @Transactional
//    public Long createPost(PostRequestDTO postRequest, List<MultipartFile> multipartFiles, String writerUsername) {
//        User writer = userRepository.findByUsername(writerUsername)
//                .orElseThrow(() -> new IllegalArgumentException("작성자를 찾을 수 없습니다."));
//
//        Post post = new Post();
//        post.setTitle(postRequest.getTitle());
//        post.setContent(postRequest.getContent());
//        post.setBlockComment(postRequest.isBlockComment());
//        post.setPrivate(postRequest.isPrivate());
//        post.setWriter(writer);
//
//        // 파일 업로드 처리
//        if (multipartFiles != null && !multipartFiles.isEmpty()) {
//            List<String> fileUrls = new ArrayList<>();
//            for (MultipartFile file : multipartFiles) {
//                try {
//                    String fileUrl = s3Service.uploadFile(file);
//                    fileUrls.add(fileUrl);
//                } catch (IOException e) {
//                    throw new RuntimeException("파일 업로드 실패: " + file.getOriginalFilename(), e);
//                }
//            }
//            post.setAttachments(fileUrls); // 파일 URL 저장
//        }
//
//        return postRepository.save(post).getId();
//    }
    @Transactional
    public void updatePost(Long postId, PostRequestDTO postRequest, List<MultipartFile> multipartFiles, String writerUsername) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getWriter().getUsername().equals(writerUsername)) {
            throw new IllegalArgumentException("작성자만 게시글을 수정할 수 있습니다.");
        }

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setBlockComment(postRequest.isBlockComment());
        post.setPrivate(postRequest.isPrivate());

        // 새로운 첨부파일 업로드
        if (multipartFiles != null) {
            List<String> fileUrls = multipartFiles.stream()
                    .map(file -> {
                        try {
                            return s3Service.uploadFile(file);
                        } catch (IOException e) {
                            throw new RuntimeException("파일 업로드 실패: " + file.getOriginalFilename(), e);
                        }
                    })
                    .collect(Collectors.toList());
            post.setAttachments(fileUrls);
        }

        postRepository.save(post);
    }

    /**
     * 게시글 목록 조회
     */
    @Transactional
    public List<PostResponseDTO> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAll(pageable);

        // 순서를 매겨서 DTO로 변환
        AtomicInteger startNumber = new AtomicInteger(page * size + 1); // 현재 페이지에 해당하는 시작 번호 계산
        return posts.getContent().stream()
                .map(post -> new PostResponseDTO(
                        post.getId(),
                        startNumber.getAndIncrement(), // 번호 계산
                        post.getTitle(),
                        post.getContent(),
                        post.getWriter().getUsername(),
                        post.getAttachments()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 게시글 총 개수 조회
     */
    @Transactional
    public Long getPostCount(String type) {
        if ("normal".equalsIgnoreCase(type)) {
            return postRepository.countByIsPrivate(false); // 비공개가 아닌 게시글 개수
        } else {
            return postRepository.count(); // 모든 게시글 개수
        }
    }

    @Transactional
    public PostResponseDTO getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        return new PostResponseDTO(
                post.getId(),
                Math.toIntExact(post.getId()), // 또는 적절한 로직으로 번호 설정
                post.getTitle(),
                post.getContent(),
                post.getWriter().getUsername(),
                post.getAttachments()
        );
    }

    @Transactional
    public List<PostResponseDTO> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.searchByKeyword(keyword, pageable);

        AtomicInteger startNumber = new AtomicInteger(page * size + 1);
        return posts.getContent().stream()
                .map(post -> new PostResponseDTO(
                        post.getId(),
                        startNumber.getAndIncrement(),
                        post.getTitle(),
                        post.getContent(),
                        post.getWriter().getUsername(),
                        post.getAttachments()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PostResponseDTO> searchPostsByTag(String tag, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByTagsContaining(tag, pageable);

        AtomicInteger startNumber = new AtomicInteger(page * size + 1);
        return posts.getContent().stream()
                .map(post -> new PostResponseDTO(
                        post.getId(),
                        startNumber.getAndIncrement(),
                        post.getTitle(),
                        post.getContent(),
                        post.getWriter().getUsername(),
                        post.getAttachments()
                ))
                .collect(Collectors.toList());
    }
}