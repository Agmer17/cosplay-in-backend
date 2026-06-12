package com.agmerrizky.cosplayin.posts.dto;

import com.agmerrizky.cosplayin.common.type.PostType;
import com.agmerrizky.cosplayin.common.type.PostsVisibility;
import com.agmerrizky.cosplayin.posts.dto.response.PostsMediaResponse;
import com.agmerrizky.cosplayin.posts.dto.response.PostsSummaryResponse;
import com.agmerrizky.cosplayin.posts.dto.response.PublicUserSummaryResponse;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record PostsResponse(
                UUID id,
                PublicUserSummaryResponse author,

                PostsSummaryResponse replyTo,

                PostsSummaryResponse repostOf,

                PostsSummaryResponse quoteOf,

                String content,
                PostType postType,
                PostsVisibility visibility,

                List<PostsMediaResponse> media,

                int likeCount,
                int repostCount,
                int replyCount,
                int quoteCount,
                int bookmarkCount,

                boolean isLiked,

                boolean isSensitive,
                LocalDateTime createdAt,
                LocalDateTime updatedAt,
                LocalDateTime deletedAt) {
}