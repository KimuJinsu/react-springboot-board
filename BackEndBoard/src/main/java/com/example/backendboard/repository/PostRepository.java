package com.example.backendboard.repository;

import com.example.backendboard.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.attachments WHERE p.id = :postId")
    Optional<Post> findByIdWithAttachments(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.tags WHERE p.id = :postId")
    Optional<Post> findByIdWithTags(@Param("postId") Long postId);

    // 키워드 검색
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 태그 검색
    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t.name = :tag")
    Page<Post> searchByTag(@Param("tag") String tag, Pageable pageable);

    // 게시글 개수 조회
    @Query("SELECT COUNT(p) FROM Post p WHERE p.privatePost = false") // 비공개 제외
    Long countNormal();

    @Query("SELECT COUNT(p) FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Long countByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(DISTINCT p) FROM Post p JOIN p.tags t WHERE t.name = :tag")
    Long countByTag(@Param("tag") String tag);

    @Query("SELECT p FROM Post p ORDER BY p.id DESC")
    Page<Post> findAllByOrderByIdDesc(Pageable pageable);

//    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.attachments LEFT JOIN FETCH p.tags WHERE p.id = :postId")
//    Optional<Post> findByIdWithAttachmentsAndTags(@Param("postId") Long postId);
@Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.attachments LEFT JOIN FETCH p.tags WHERE p.id = :postId")
Optional<Post> findByIdWithAttachmentsAndTags(@Param("postId") Long postId);




}