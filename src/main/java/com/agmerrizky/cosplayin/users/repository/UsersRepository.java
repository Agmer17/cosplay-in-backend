package com.agmerrizky.cosplayin.users.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agmerrizky.cosplayin.common.entity.Users;
import com.agmerrizky.cosplayin.common.type.OauthProvider;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByOauthProviderAndOauthProviderId(OauthProvider oauthProvider, String oauthProviderId);
}
