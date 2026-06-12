package com.agmerrizky.cosplayin.posts.mapper;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    public static PostsSummaryResponse toSummaryResponse(Posts post, Set<UUID> likedPosts) {
        if (post == null) {
            return null;
        }

        return PostsSummaryResponse.builder()
                .id(post.getId())
                .author(toUserSummary(post.getUser()))
                .content(post.getContent())
                .postType(post.getPostType())
                .visibility(post.getVisibility())
                .media(post.getMedia() == null
                        ? List.of()
                        : post.getMedia().stream()
                                .map(PostsMapper::toMediaResponse)
                                .toList())
                .likeCount(post.getLikeCount())
                .repostCount(post.getRepostCount())
                .replyCount(post.getReplyCount())
                .quoteCount(post.getQuoteCount())
                .isSensitive(post.isSensitive())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public static List<PostsSummaryResponse> toSummaryResponse(List<Posts> posts, Set<UUID> ids) {
        if (posts == null)
            return List.of();

        return posts.stream()
                .map((p) -> toSummaryResponse(p, ids))
                .toList();
    }

    public static PostsResponse toResponse(Posts post, Set<UUID> likedIds) {
        return PostsResponse.builder()
                .id(post.getId())
                .author(toUserSummary(post.getUser()))
                .replyTo(toSummaryResponse(post.getReplyTo(), likedIds))
                .repostOf(toSummaryResponse(post.getRepostOf(), likedIds))
                .quoteOf(toSummaryResponse(post.getQuoteOf(), likedIds))
                .content(post.getContent())
                .postType(post.getPostType())
                .visibility(post.getVisibility())
                .media(post.getMedia() == null
                        ? List.of()
                        : post.getMedia().stream()
                                .map(PostsMapper::toMediaResponse)
                                .toList())
                .likeCount(post.getLikeCount())
                .repostCount(post.getRepostCount())
                .replyCount(post.getReplyCount())
                .quoteCount(post.getQuoteCount())
                .bookmarkCount(post.getBookmarkCount())
                .isSensitive(post.isSensitive())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .deletedAt(post.getDeletedAt())
                .build();
    }
}
