package com.agmerrizky.cosplayin.users.dto;

import java.util.UUID;

import com.agmerrizky.cosplayin.common.type.UserRoleType;
import com.agmerrizky.cosplayin.common.type.UsersStatusType;

import lombok.Builder;

@Builder
public record UsersSessionDto(
        UUID id,
        UserRoleType role,
        UsersStatusType status) {

}
