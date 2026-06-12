package com.agmerrizky.cosplayin.posts.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.agmerrizky.cosplayin.common.anotations.CurrentUser;
import com.agmerrizky.cosplayin.common.anotations.RequireAuth;
import com.agmerrizky.cosplayin.common.api.SuccessResponse;
import com.agmerrizky.cosplayin.common.type.CurrentUserContext;
import com.agmerrizky.cosplayin.posts.service.LikesService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikesController {

    private final LikesService service;

    @PostMapping("/create/{id}")
    @RequireAuth
    public ResponseEntity<SuccessResponse<Object>> handleCreateLike(@PathVariable UUID id,
            @CurrentUser CurrentUserContext ctx) {
        service.createLikes(id, ctx.id());
        return ResponseEntity.ok().body(
                SuccessResponse.builder()
                        .message("successfully adding the likes to the posts")
                        .data(null)
                        .build());
    }

    @DeleteMapping("/remove/{id}")
    @RequireAuth
    public ResponseEntity<SuccessResponse<Object>> handleUnlikeThePosts(@PathVariable UUID id,
            @CurrentUser CurrentUserContext ctx) {
        service.unlikePost(ctx.id(), id);
        return ResponseEntity.ok().body(
                SuccessResponse.builder()
                        .message("successfully removing the  likes from the posts")
                        .data(null)
                        .build());
    }

}
