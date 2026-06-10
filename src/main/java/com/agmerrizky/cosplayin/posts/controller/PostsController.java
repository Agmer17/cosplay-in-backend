package com.agmerrizky.cosplayin.posts.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agmerrizky.cosplayin.common.anotations.CurrentUser;
import com.agmerrizky.cosplayin.common.anotations.RequireAuth;
import com.agmerrizky.cosplayin.common.api.SuccessResponse;
import com.agmerrizky.cosplayin.common.entity.Posts;
import com.agmerrizky.cosplayin.common.type.CurrentUserContext;
import com.agmerrizky.cosplayin.posts.dto.CreatePostsDto;
import com.agmerrizky.cosplayin.posts.dto.PostsResponse;
import com.agmerrizky.cosplayin.posts.dto.response.PostsSummaryResponse;
import com.agmerrizky.cosplayin.posts.mapper.PostsMapper;
import com.agmerrizky.cosplayin.posts.service.PostsService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostsController {

    private final PostsService service;

    @PostMapping("/new")
    @RequireAuth
    public ResponseEntity<SuccessResponse<PostsResponse>> handleCreatePosts(@ModelAttribute CreatePostsDto dto,
            @CurrentUser CurrentUserContext curr) {
        Posts newPosts = service.createPosts(dto, curr);

        return ResponseEntity.ok().body(
                SuccessResponse.<PostsResponse>builder()
                        .message("successfully creating a new posts")
                        .data(PostsMapper.toResponse(newPosts))
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<PostsResponse>> getPostsDetails(@PathVariable UUID id) {
        Posts data = service.getPostsById(id);

        return ResponseEntity.ok().body(
                SuccessResponse.<PostsResponse>builder()
                        .message("successfully getting the users data")
                        .data(PostsMapper.toResponse(data))
                        .build());
    }

    @GetMapping("/replies/{id}")
    public ResponseEntity<SuccessResponse<List<PostsSummaryResponse>>> getMethodName(@PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page) {

        List<Posts> replies = service.getReplyFromPosts(id, page);
        return ResponseEntity.ok()
                .body(
                        SuccessResponse.<List<PostsSummaryResponse>>builder()
                                .message("successfully getting the reply")
                                .data(PostsMapper.toSummaryResponse(replies))
                                .build());
    }

}
