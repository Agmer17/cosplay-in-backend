package com.agmerrizky.cosplayin.posts.dto.response;

import java.util.UUID;

public record PublicUserSummaryResponse(
        UUID id,
        String fullName,
        String profilePicture,
        String bannerImage) {
}