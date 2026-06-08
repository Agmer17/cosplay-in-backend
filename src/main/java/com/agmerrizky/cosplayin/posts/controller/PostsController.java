package com.agmerrizky.cosplayin.posts.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agmerrizky.cosplayin.common.anotations.CurrentUser;
import com.agmerrizky.cosplayin.common.anotations.RequireAuth;
import com.agmerrizky.cosplayin.common.api.SuccessResponse;
import com.agmerrizky.cosplayin.common.entity.Posts;
import com.agmerrizky.cosplayin.common.type.CurrentUserContext;
import com.agmerrizky.cosplayin.posts.dto.CreatePostsDto;
import com.agmerrizky.cosplayin.posts.service.PostsService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostsController {

    private final PostsService service;

    @PostMapping("/new")
    @RequireAuth
    public ResponseEntity<SuccessResponse<Posts>> handleCreatePosts(@ModelAttribute CreatePostsDto dto,
            @CurrentUser CurrentUserContext curr) {
        Posts newPosts = service.createPosts(dto, curr);

        return ResponseEntity.ok().body(
                SuccessResponse.<Posts>builder()
                        .message("successfully creating a new posts")
                        .data(newPosts)
                        .build());
    }

}
