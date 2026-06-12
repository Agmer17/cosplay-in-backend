package com.agmerrizky.cosplayin.common.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bookmarks", indexes = {
                @Index(name = "idx_bookmarks_user_id", columnList = "user_id"),
                @Index(name = "idx_bookmarks_post_id", columnList = "post_id"),
                @Index(name = "idx_bookmarks_created_at", columnList = "created_at DESC")
}, uniqueConstraints = {
                @UniqueConstraint(name = "uq_bookmarks_user_post", columnNames = { "user_id", "post_id" })
})
@NamedEntityGraph(name = "Bookmarks.withPost", attributeNodes = {
                @NamedAttributeNode("post")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bookmarks {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private Users user;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "post_id", nullable = false)
        private Posts post;

        @CreationTimestamp
        @Column(updatable = false, nullable = false)
        private LocalDateTime createdAt;
}