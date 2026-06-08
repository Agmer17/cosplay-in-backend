package com.agmerrizky.cosplayin.posts.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agmerrizky.cosplayin.common.entity.Posts;

@Repository
public interface PostsRepository extends JpaRepository<Posts, UUID> {

}
