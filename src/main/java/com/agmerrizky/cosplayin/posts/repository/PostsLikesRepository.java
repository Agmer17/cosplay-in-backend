package com.agmerrizky.cosplayin.posts.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.agmerrizky.cosplayin.common.entity.PostsLikes;

import io.lettuce.core.dynamic.annotation.Param;

public interface PostsLikesRepository extends JpaRepository<PostsLikes, UUID> {

  @Query("""
          SELECT pl.post.id
          FROM PostsLikes pl
          WHERE pl.user.id = :userId
            AND pl.post.id IN :postIds
      """)
  Set<UUID> findLikedPostIds(
      UUID userId,
      List<UUID> postIds);

  @Modifying
  @Query("""
      DELETE FROM PostsLikes pl
      WHERE pl.user.id = :userId
        AND pl.post.id = :postId
      """)
  int deleteLike(
      @Param("userId") UUID userId,
      @Param("postId") UUID postId);
}
