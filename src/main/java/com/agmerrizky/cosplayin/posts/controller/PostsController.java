package com.agmerrizky.cosplayin.posts.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agmerrizky.cosplayin.common.anotations.CurrentUser;
import com.agmerrizky.cosplayin.common.anotations.RequireAuth;
import com.agmerrizky.cosplayin.common.anotations.RequireRole;
import com.agmerrizky.cosplayin.common.api.SuccessResponse;
import com.agmerrizky.cosplayin.common.entity.Posts;
import com.agmerrizky.cosplayin.common.type.CurrentUserContext;
import com.agmerrizky.cosplayin.posts.dto.CreatePostsDto;
import com.agmerrizky.cosplayin.posts.dto.PostsResponse;
import com.agmerrizky.cosplayin.posts.dto.response.PostsSummaryResponse;
import com.agmerrizky.cosplayin.posts.mapper.PostsMapper;
import com.agmerrizky.cosplayin.posts.service.LikesService;
import com.agmerrizky.cosplayin.posts.service.PostsService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostsController {

        private final PostsService service;
        private final LikesService likesService;

        @PostMapping("/create")
        @RequireAuth
        public ResponseEntity<SuccessResponse<PostsResponse>> handleCreatePosts(@ModelAttribute CreatePostsDto dto,
                        @CurrentUser CurrentUserContext curr) {
                Posts newPosts = service.createPosts(dto, curr);
                Set<UUID> likes = Set.of();
                return ResponseEntity.ok().body(
                                SuccessResponse.<PostsResponse>builder()
                                                .message("successfully creating a new posts")
                                                .data(PostsMapper.toResponse(newPosts, likes))
                                                .build());
        }

        @GetMapping("/{id}")
        public ResponseEntity<SuccessResponse<PostsResponse>> getPostsDetails(@PathVariable UUID id,
                        @CurrentUser CurrentUserContext curr) {
                Posts data = service.getPostsById(id);

                Set<UUID> likedIds = Set.of();
                if (curr != null) {
                        likedIds = likesService.findLikedPosts(id, data.getId());
                }
                return ResponseEntity.ok().body(
                                SuccessResponse.<PostsResponse>builder()
                                                .message("successfully getting the users data")
                                                .data(PostsMapper.toResponse(data, likedIds))
                                                .build());
        }

        @GetMapping("/replies/{id}")
        public ResponseEntity<SuccessResponse<List<PostsSummaryResponse>>> handleGetReplies(@PathVariable UUID id,
                        @RequestParam(defaultValue = "0") int page, @CurrentUser CurrentUserContext curr) {

                List<Posts> replies = service.getReplyFromPosts(id, page);

                Set<UUID> likedIds = Set.of();
                if (curr != null) {
                        likedIds = likesService.findLikedPosts(id, replies.stream().map(Posts::getId).toList());
                }
                return ResponseEntity.ok()
                                .body(
                                                SuccessResponse.<List<PostsSummaryResponse>>builder()
                                                                .message("successfully getting the reply")
                                                                .data(PostsMapper.toSummaryResponse(replies, likedIds))
                                                                .build());
        }

        @DeleteMapping("/delete/{id}")
        @RequireAuth
        @RequireRole("ADMIN")
        public ResponseEntity<SuccessResponse<Object>> handleDeletePosts(@PathVariable UUID id) {
                service.deletePosts(id);
                return ResponseEntity.ok().body(
                                SuccessResponse.<Object>builder()
                                                .data(null)
                                                .message("succesfully delete the posts")
                                                .build());
        }

}
