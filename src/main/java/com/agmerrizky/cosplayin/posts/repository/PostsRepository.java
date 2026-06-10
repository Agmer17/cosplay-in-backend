package com.agmerrizky.cosplayin.posts.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agmerrizky.cosplayin.common.entity.Posts;

@Repository
public interface PostsRepository extends JpaRepository<Posts, UUID> {

    @EntityGraph(value = "Posts.withDetails", type = EntityGraph.EntityGraphType.FETCH)
    Page<Posts> findByReplyTo_Id(UUID postId, Pageable pageable);
}
