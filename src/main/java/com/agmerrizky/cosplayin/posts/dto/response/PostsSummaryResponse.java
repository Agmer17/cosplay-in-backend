package com.agmerrizky.cosplayin.posts.dto.response;

import com.agmerrizky.cosplayin.common.type.PostType;
import com.agmerrizky.cosplayin.common.type.PostsVisibility;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}