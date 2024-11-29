package com.example.backendboard.repository;

import com.example.backendboard.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT COUNT(p) FROM Post p WHERE p.isPrivate = :isPrivate")
    Long countByIsPrivate(boolean isPrivate);

    @Override
    Page<Post> findAll(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tag")
    Page<Post> findByTagsContaining(@Param("tag") String tag, Pageable pageable);

}