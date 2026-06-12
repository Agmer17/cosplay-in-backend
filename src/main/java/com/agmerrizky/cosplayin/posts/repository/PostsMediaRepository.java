package com.agmerrizky.cosplayin.posts.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agmerrizky.cosplayin.common.entity.PostsMedia;

public interface PostsMediaRepository extends JpaRepository<PostsMedia, UUID> {

}