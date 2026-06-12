package com.agmerrizky.cosplayin.posts.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.agmerrizky.cosplayin.common.entity.Posts;

public interface PostsRepository extends JpaRepository<Posts, UUID> {

    @EntityGraph(value = "Posts.withDetails", type = EntityGraph.EntityGraphType.FETCH)
    Page<Posts> findByReplyTo_Id(UUID postId, Pageable pageable);

    @EntityGraph(value = "Posts.withDetails")
    Optional<Posts> findByIdAndDeletedAtIsNull(UUID id);
}
