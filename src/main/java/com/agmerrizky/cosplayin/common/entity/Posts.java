package com.agmerrizky.cosplayin.common.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import com.agmerrizky.cosplayin.common.type.PostType;
import com.agmerrizky.cosplayin.common.type.PostsVisibility;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "posts", indexes = {
        @Index(name = "idx_posts_user_id", columnList = "user_id"),
        @Index(name = "idx_posts_reply_to", columnList = "reply_to_post_id"),
        @Index(name = "idx_posts_created_at", columnList = "created_at DESC")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(name = "Posts.withDetails", attributeNodes = {
        @NamedAttributeNode("media"),
        @NamedAttributeNode("user")
})
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relasi ke user — ManyToOne, FK ada di sini
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // Self-referencing: reply ke post lain (thread)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_post_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Posts replyTo;

    // Self-referencing: repost (RT tanpa komentar)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repost_of_post_id")
    private Posts repostOf;

    // Self-referencing: quote tweet
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_of_post_id")
    private Posts quoteOf;

    @Column(length = 280)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType postType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostsVisibility visibility;

    // Counter — disimpan di kolom agar query feed cepat, bukan COUNT() tiap kali
    @Column(nullable = false)
    @Builder.Default
    private int likeCount = 0;
    @Column(nullable = false)
    @Builder.Default
    private int repostCount = 0;
    @Column(nullable = false)
    @Builder.Default
    private int replyCount = 0;
    @Column(nullable = false)
    @Builder.Default
    private int quoteCount = 0;
    @Column(nullable = false)
    @Builder.Default
    private int bookmarkCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean isSensitive = false;

    // Media attachment
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @JsonManagedReference
    private List<PostsMedia> media;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}