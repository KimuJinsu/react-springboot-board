package com.example.backendboard.service;

import com.example.backendboard.dto.AttachmentResponseDTO;
import com.example.backendboard.dto.PostRequestDTO;
import com.example.backendboard.dto.PostResponseDTO;
import com.example.backendboard.entity.Attachment;
import com.example.backendboard.entity.Post;
import com.example.backendboard.entity.Tag;
import com.example.backendboard.entity.User;
import com.example.backendboard.repository.PostRepository;
import com.example.backendboard.repository.AttachmentRepository;
import com.example.backendboard.repository.UserRepository;
import com.example.backendboard.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AttachmentRepository attachmentRepository;
    private final S3Service s3Service; // S3 업로드를 사용한다고 가정
    private final UserRepository userRepository;

    /**
     * 새 게시글 작성
     */
    @Transactional
    public Long createPost(PostRequestDTO postRequest, List<MultipartFile> multipartFiles, String username) {
        // 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // Post 엔티티 생성
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setBlockComment(postRequest.isBlockComment());
        post.setPrivatePost(postRequest.isPrivatePost());
        post.setWriter(user); // 작성자 설정

        // 태그 처리
        if (postRequest.getTags() != null && !postRequest.getTags().isEmpty()) {
            List<Tag> tags = postRequest.getTags().stream()
                    .map(tagName -> {
                        Tag tag = new Tag();
                        tag.setName(tagName);
                        tag.setPost(post);
                        return tag;
                    })
                    .collect(Collectors.toList());
            post.setTags(tags);
        }

        // Post 저장 후 새로운 변수에 할당
        Post savedPost = postRepository.save(post);

        // 첨부파일 처리
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            List<Attachment> attachments = new ArrayList<>();
            for (MultipartFile file : multipartFiles) {
                try {
                    String fileUrl = s3Service.uploadFile(file); // S3에 파일 업로드
                    Attachment attachment = new Attachment();
                    attachment.setRealFileName(file.getOriginalFilename());
                    attachment.setS3Url(fileUrl); // S3 URL 설정
                    attachment.setPost(savedPost);
                    attachmentRepository.save(attachment);
                    attachments.add(attachment);
                } catch (IOException e) {
                    throw new RuntimeException("파일 업로드 실패: " + file.getOriginalFilename(), e);
                }
            }
            savedPost.setAttachments(attachments); // 파일 정보 연결
        }

        return savedPost.getId();
    }

    /**
     * 게시글 조회
     */
    @Transactional(readOnly = true)
    public PostResponseDTO getPost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (post.isPrivatePost() && (username == null || !post.getWriter().getUsername().equals(username))) {
            throw new RuntimeException("비공개 게시글입니다.");
        }

        return new PostResponseDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.isPrivatePost(),
                post.isBlockComment(),
                post.getTags() != null
                        ? post.getTags().stream().map(Tag::getName).collect(Collectors.toList())
                        : List.of(),
                post.getAttachments() != null
                        ? post.getAttachments().stream()
                        .map(attachment -> new AttachmentResponseDTO(
                                attachment.getId(),
                                attachment.getRealFileName(),
                                attachment.getS3Url()))
                        .collect(Collectors.toList())
                        : List.of(),
                post.getWriter().getUsername(), // 작성자 이름
                post.getWriter().getProfileImageUrl(), // 작성자 프로필 이미지 URL
                post.getPostDate(), // 작성일 매핑
                post.getLikes() != null ? post.getLikes().size() : 0,    // 좋아요 수로 변환
                post.getViews()     // 추가된 필드
        );
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<PostResponseDTO> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // 변경: 내림차순으로 조회
        return postRepository.findAllByOrderByIdDesc(pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 키워드 검색
    @Transactional(readOnly = true)
    public List<PostResponseDTO> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.searchByKeyword(keyword, pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 태그 검색
    @Transactional(readOnly = true)
    public List<PostResponseDTO> searchPostsByTag(String tag, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.searchByTag(tag, pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 게시글 개수 조회
    @Transactional(readOnly = true)
    public Long getPostCount(String type, String data) {
        switch (type.toLowerCase()) {
            case "normal":
                return postRepository.countNormal();
            case "search":
                return postRepository.countByKeyword(data);
            case "tag":
                return postRepository.countByTag(data);
            default:
                throw new IllegalArgumentException("Invalid count type");
        }
    }

    // Post를 DTO로 변환
    private PostResponseDTO convertToDTO(Post post) {
        return new PostResponseDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.isPrivatePost(),
                post.isBlockComment(),
                post.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
                post.getAttachments().stream()
                        .map(att -> new AttachmentResponseDTO(att.getId(), att.getRealFileName(), att.getS3Url()))
                        .collect(Collectors.toList()),
                post.getWriter().getUsername(), // 작성자 이름
                post.getWriter().getProfileImageUrl(), // 작성자 프로필 이미지 URL
                post.getPostDate(), // 작성일 매핑
                post.getLikes() != null ? post.getLikes().size() : 0,    // 좋아요 수로 변환
                post.getViews()     // 추가된 필드
        );
    }

    /**
     * 게시글 조회수 증가
     */
    @Transactional
    public void increaseViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        post.setViews(post.getViews() + 1);
        postRepository.save(post);
    }

    /**
     * 좋아요 기능
     */
    @Transactional
    public void likePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (post.getWriter().getUsername().equals(username)) {
            throw new RuntimeException("자신의 게시글에 좋아요를 누를 수 없습니다.");
        }

        if (post.getLikes().contains(username)) {
            post.getLikes().remove(username); // 좋아요 취소
        } else {
            post.getLikes().add(username); // 좋아요 추가
        }

        postRepository.save(post);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public void updatePost(Long postId, PostRequestDTO postRequest, List<MultipartFile> multipartFiles, String username) {
        // 게시글 조회 (기본 데이터만 로드)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!post.getWriter().getUsername().equals(username)) {
            throw new RuntimeException("게시글 수정 권한이 없습니다.");
        }

        // 게시글 수정
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setPrivatePost(postRequest.isPrivatePost());
        post.setBlockComment(postRequest.isBlockComment());

        // 태그 수정
        if (postRequest.getTags() != null) {
            // 기존 태그 삭제 후 새 태그 추가
            post.getTags().clear();
            List<Tag> tags = postRequest.getTags().stream()
                    .map(tagName -> new Tag(tagName, post)) // Tag 객체 생성
                    .collect(Collectors.toList());
            post.getTags().addAll(tags);
        }

        // 첨부파일 삭제 처리
        if (postRequest.getDeletedFileIds() != null && !postRequest.getDeletedFileIds().isEmpty()) {
            postRequest.getDeletedFileIds().forEach(fileId -> {
                attachmentRepository.findById(fileId).ifPresent(attachment -> {
                    s3Service.deleteFile(attachment.getS3Url()); // S3 파일 삭제
                    post.getAttachments().remove(attachment); // 엔티티 연관 제거
                    attachmentRepository.delete(attachment); // DB 삭제
                });
            });
        }

        // 새로운 첨부파일 추가 처리
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            multipartFiles.forEach(file -> {
                try {
                    String fileUrl = s3Service.uploadFile(file); // S3에 파일 업로드
                    Attachment attachment = new Attachment();
                    attachment.setRealFileName(file.getOriginalFilename());
                    attachment.setS3Url(fileUrl);
                    attachment.setPost(post);
                    attachmentRepository.save(attachment);
                    post.getAttachments().add(attachment); // 게시글과 첨부파일 연결
                } catch (IOException e) {
                    throw new RuntimeException("파일 업로드 실패: " + file.getOriginalFilename(), e);
                }
            });
        }

        // 게시글 저장
        postRepository.save(post);
    }

//    @Transactional
//    public void updatePost(Long postId, PostRequestDTO postRequest, List<MultipartFile> multipartFiles, String username) {
//        // 게시글 조회 (연관된 attachments와 tags 함께 로드)
//        Post post = postRepository.findByIdWithAttachmentsAndTags(postId)
//                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
//
//        // 작성자 확인
//        if (!post.getWriter().getUsername().equals(username)) {
//            throw new RuntimeException("게시글 수정 권한이 없습니다.");
//        }
//
//        // 게시글 수정
//        post.setTitle(postRequest.getTitle());
//        post.setContent(postRequest.getContent());
//        post.setPrivatePost(postRequest.isPrivatePost());
//        post.setBlockComment(postRequest.isBlockComment());
//
//
//        // 태그 수정
//        if (postRequest.getTags() != null) {
//            // 기존 태그 삭제
//            post.getTags().clear();
//            // 새로운 태그 추가
//            List<Tag> tags = postRequest.getTags().stream()
//                    .map(tagName -> {
//                        Tag tag = new Tag();
//                        tag.setName(tagName);
//                        tag.setPost(post);
//                        return tag;
//                    })
//                    .collect(Collectors.toList());
//            post.setTags(tags);
//        }
//
//        // 첨부파일 삭제
//        if (postRequest.getDeletedFileIds() != null && !postRequest.getDeletedFileIds().isEmpty()) {
//            for (Long fileId : postRequest.getDeletedFileIds()) {
//                Attachment attachment = attachmentRepository.findById(fileId)
//                        .orElseThrow(() -> new RuntimeException("첨부파일을 찾을 수 없습니다."));
//                // S3에서 파일 삭제
//                s3Service.deleteFile(attachment.getS3Url());
//                attachmentRepository.delete(attachment);
//            }
//        }
//
//        // 새로운 첨부파일 추가
//        if (multipartFiles != null && !multipartFiles.isEmpty()) {
//            List<Attachment> attachments = new ArrayList<>();
//            for (MultipartFile file : multipartFiles) {
//                try {
//                    String fileUrl = s3Service.uploadFile(file); // S3에 파일 업로드
//                    Attachment attachment = new Attachment();
//                    attachment.setRealFileName(file.getOriginalFilename());
//                    attachment.setS3Url(fileUrl); // S3 URL 설정
//                    attachment.setPost(post);
//                    attachmentRepository.save(attachment);
//                    attachments.add(attachment);
//                } catch (IOException e) {
//                    throw new RuntimeException("파일 업로드 실패: " + file.getOriginalFilename(), e);
//                }
//            }
//            post.getAttachments().addAll(attachments); // 파일 정보 연결
//        }
//
//        // 게시글 저장
//        postRepository.save(post);
//    }


    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!post.getWriter().getUsername().equals(username)) {
            throw new RuntimeException("게시글 삭제 권한이 없습니다.");
        }

        // S3에서 첨부파일 삭제
        if (post.getAttachments() != null) {
            for (Attachment attachment : post.getAttachments()) {
                s3Service.deleteFile(attachment.getS3Url());
            }
        }

        postRepository.delete(post);
    }
}
