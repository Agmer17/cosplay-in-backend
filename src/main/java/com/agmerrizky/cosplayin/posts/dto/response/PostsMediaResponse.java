package com.agmerrizky.cosplayin.posts.dto.response;

import com.agmerrizky.cosplayin.common.type.MediaType;
import java.util.UUID;

public record PostsMediaResponse(
                UUID id,
                String mediaUrl,
                MediaType mediaType,
                String thumbnailUrl, // null kalau bukan video
                Integer width,
                Integer height,
                Integer durationSeconds, // null kalau bukan video
                int displayOrder) {
}