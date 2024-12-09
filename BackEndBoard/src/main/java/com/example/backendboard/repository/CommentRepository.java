package com.example.backendboard.repository;

import com.example.backendboard.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndIsDeletedFalse(Long postId);

    List<Comment> findByPostId(Long postId);

}