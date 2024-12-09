package com.example.backendboard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private boolean privatePost;

    @Column(nullable = false)
    private boolean blockComment;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Tag> tags = new ArrayList<>(); // 태그를 Tag 엔티티로 관리

    @ManyToOne(fetch = FetchType.LAZY) // 작성자와의 연관관계 추가
    @JoinColumn(name = "writer_id", nullable = false) // 외래 키 컬럼명 설정
    @ToString.Exclude
    private User writer; // 작성자

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime postDate; // 작성일 필드 추가

    @ElementCollection(fetch = FetchType.LAZY) // 좋아요를 누른 사용자 목록
    private List<String> likes = new ArrayList<>();

    private int views = 0;

    public void increaseViewCount() {
        this.views++;
    }

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();
}
