package com.agmerrizky.cosplayin.common.type;

import java.util.UUID;

public record CurrentUserContext(
        UUID id,
        UserRoleType role) {
}
