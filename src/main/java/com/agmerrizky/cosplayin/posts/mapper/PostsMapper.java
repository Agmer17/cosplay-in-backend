package com.agmerrizky.cosplayin.posts.mapper;

import java.util.List;

import com.agmerrizky.cosplayin.common.entity.Posts;
import com.agmerrizky.cosplayin.common.entity.PostsMedia;
import com.agmerrizky.cosplayin.common.entity.Users;
import com.agmerrizky.cosplayin.posts.dto.PostsResponse;
import com.agmerrizky.cosplayin.posts.dto.response.PostsMediaResponse;
import com.agmerrizky.cosplayin.posts.dto.response.PostsSummaryResponse;
import com.agmerrizky.cosplayin.posts.dto.response.PublicUserSummaryResponse;

public class PostsMapper {
    public static PostsMediaResponse toMediaResponse(PostsMedia media) {
        return new PostsMediaResponse(
                media.getId(),
                media.getMediaUrl(),
                media.getMediaType(),
                media.getThumbnailUrl(),
                media.getWidth(),
                media.getHeight(),
                media.getDurationSeconds(),
                media.getDisplayOrder());
    }

    public static PublicUserSummaryResponse toUserSummary(Users user) {
        return new PublicUserSummaryResponse(
                user.getId(),
                user.getFullName(),
                user.getProfilePicture(),
                user.getBannerPicture());
    }

    public static PostsSummaryResponse toSummaryResponse(Posts post) {
        if (post == null)
            return null;

        return new PostsSummaryResponse(
                post.getId(),
                toUserSummary(post.getUser()),
                post.getContent(),
                post.getPostType(),
                post.getVisibility(),
                post.getMedia() == null ? List.of()
                        : post.getMedia().stream().map(PostsMapper::toMediaResponse).toList(),
                post.getLikeCount(),
                post.getRepostCount(),
                post.getReplyCount(),
                post.getQuoteCount(),
                post.isSensitive(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }

    public static List<PostsSummaryResponse> toSummaryResponse(List<Posts> posts) {
        if (posts == null)
            return List.of();

        return posts.stream()
                .map(PostsMapper::toSummaryResponse)
                .toList();
    }

    public static PostsResponse toResponse(Posts post) {
        return new PostsResponse(
                post.getId(),
                toUserSummary(post.getUser()),
                toSummaryResponse(post.getReplyTo()),
                toSummaryResponse(post.getRepostOf()),
                toSummaryResponse(post.getQuoteOf()),
                post.getContent(),
                post.getPostType(),
                post.getVisibility(),
                post.getMedia() == null ? List.of()
                        : post.getMedia().stream().map(PostsMapper::toMediaResponse).toList(),
                post.getLikeCount(),
                post.getRepostCount(),
                post.getReplyCount(),
                post.getQuoteCount(),
                post.getBookmarkCount(),
                post.isSensitive(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }
}
