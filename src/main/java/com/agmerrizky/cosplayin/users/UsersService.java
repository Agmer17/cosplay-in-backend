package com.agmerrizky.cosplayin.users;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.agmerrizky.cosplayin.common.entity.Users;
import com.agmerrizky.cosplayin.common.type.OauthProvider;
import com.agmerrizky.cosplayin.common.type.UserRoleType;

import jakarta.validation.ConstraintViolationException;

import com.agmerrizky.cosplayin.common.exceptions.BadRequestsException;
import com.agmerrizky.cosplayin.common.exceptions.ConflictDataException;
import com.agmerrizky.cosplayin.common.exceptions.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository repo;

    public Users GetByOauthProviderId(OauthProvider provider, String id) {
        return repo.findByOauthProviderAndOauthProviderId(provider, id).orElse(null);
    }

    public Users createUser(String fullname, String email, OauthProvider provider, String providerId) {
        Users user = Users.builder()
                .fullName(fullname)
                .email(email)
                .oauthProvider(provider)
                .oauthProviderId(providerId)
                .role(UserRoleType.USER)
                .build();
        try {
            repo.save(user);
        } catch (IllegalArgumentException e) {
            throw new BadRequestsException("Invalid Argument provided to create a user");
        } catch (ConstraintViolationException e) {
            throw new BadRequestsException("the provided argument is not sufficient to create a user");
        } catch (DataIntegrityViolationException e) {
            throw new ConflictDataException("user data integrity is violated : " + e.getMessage());
        }

        return user;
    }

    public List<Users> getAllUsers() {
        return repo.findAll();
    }

    public Users getUserById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("user with this id not found"));
    }
}
