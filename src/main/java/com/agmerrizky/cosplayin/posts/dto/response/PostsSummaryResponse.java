package com.agmerrizky.cosplayin.posts.dto.response;

import com.agmerrizky.cosplayin.common.type.PostType;
import com.agmerrizky.cosplayin.common.type.PostsVisibility;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record PostsSummaryResponse(
                UUID id,
                PublicUserSummaryResponse author,
                String content,
                PostType postType,
                PostsVisibility visibility,
                List<PostsMediaResponse> media,
                int likeCount,
                int repostCount,
                int replyCount,
                int quoteCount,
                boolean isSensitive,
                boolean isLiked,
                LocalDateTime createdAt,
                LocalDateTime updatedAt,
                LocalDateTime deletedAt) {
}