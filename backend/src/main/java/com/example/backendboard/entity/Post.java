package com.example.backendboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    private boolean blockComment;

    private boolean isPrivate;

    @ElementCollection
    @CollectionTable(name = "post_attachments", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "attachment_url")
    private List<String> attachments;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private User writer;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();
}