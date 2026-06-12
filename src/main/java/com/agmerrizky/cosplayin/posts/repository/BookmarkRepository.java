package com.agmerrizky.cosplayin.posts.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.agmerrizky.cosplayin.common.entity.Bookmarks;

public interface BookmarkRepository extends JpaRepository<Bookmarks, UUID> {

    Optional<Bookmarks> findByUserIdAndPostId(UUID userId, UUID postId);

    @EntityGraph(attributePaths = { "post", "post.media", "post.user" })
    List<Bookmarks> findByUserId(UUID userId);
}
