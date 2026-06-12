package com.agmerrizky.cosplayin.common.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts_likes", uniqueConstraints = {
        @UniqueConstraint(name = "uq_posts_likes_user_post", columnNames = { "user_id", "post_id" })
}, indexes = {
        @Index(name = "idx_posts_likes_post_id", columnList = "post_id"),
        @Index(name = "idx_posts_likes_user_id", columnList = "user_id"),
        @Index(name = "idx_posts_likes_created_at", columnList = "created_at DESC")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostsLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    Posts post;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    LocalDateTime createdAt;

}
