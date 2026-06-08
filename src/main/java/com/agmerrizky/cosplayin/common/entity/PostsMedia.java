package com.agmerrizky.cosplayin.common.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.agmerrizky.cosplayin.common.type.MediaType;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts_media")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostsMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // FK ke posts, sisi "many" — wajib @ManyToOne + @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonBackReference
    private Posts post;

    @Column(nullable = false)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    // Thumbnail untuk video
    private String thumbnailUrl;

    // Dimensi — berguna untuk placeholder sebelum gambar load
    private Integer width;
    private Integer height;

    private Integer durationSeconds;

    // Urutan tampil di carousel (0-based)
    @Column(nullable = false)
    @Builder.Default
    private int displayOrder = 0;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
}