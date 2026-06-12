package com.agmerrizky.cosplayin.users.service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.agmerrizky.cosplayin.common.entity.Users;
import com.agmerrizky.cosplayin.common.type.CurrentUserContext;
import com.agmerrizky.cosplayin.common.type.OauthProvider;
import com.agmerrizky.cosplayin.common.type.UserRoleType;
import com.agmerrizky.cosplayin.common.type.UsersStatusType;
import com.agmerrizky.cosplayin.users.dto.UpdateUserDto;
import com.agmerrizky.cosplayin.users.dto.UsersSessionDto;
import com.agmerrizky.cosplayin.users.repository.UsersRepository;
import com.agmerrizky.cosplayin.utils.ServerStorage;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintViolationException;

import com.agmerrizky.cosplayin.common.exceptions.BadRequestsException;
import com.agmerrizky.cosplayin.common.exceptions.ConflictDataException;
import com.agmerrizky.cosplayin.common.exceptions.FatalError;
import com.agmerrizky.cosplayin.common.exceptions.ForbiddenAccessException;
import com.agmerrizky.cosplayin.common.exceptions.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository repo;
    private final ServerStorage storage;
    private final String USER_PROFILE_PIC_DIR = "profile_picture";
    private final String USER_BANNER_PIC_DIR = "banner";
    private final String USER_UPLOADS_DIR = "user";

    public Users GetByOauthProviderId(OauthProvider provider, String id) {
        return repo.findByOauthProviderAndOauthProviderId(provider, id).orElse(null);
    }

    public List<Users> getAllUsers() {
        return repo.findAll();
    }

    public Users createUser(String fullname, String email, OauthProvider provider, String providerId,
            String profilePicture) {
        Users user = Users.builder()
                .fullName(fullname)
                .email(email)
                .oauthProvider(provider)
                .oauthProviderId(providerId)
                .role(UserRoleType.ADMIN)
                .userStatus(UsersStatusType.ACTIVE)
                .profilePicture(profilePicture)
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

    public Users getUserById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("user with this id not found"));
    }

    @Cacheable(value = "users", key = "#id")
    public UsersSessionDto getUsersSessionData(UUID id) {
        Users user = repo.findById(id).orElse(null);
        if (user == null) {
            return null;
        }

        UsersSessionDto dto = new UsersSessionDto(id, user.getRole(), user.getUserStatus());

        return dto;

    }

    @Transactional
    public Users updateUsers(UpdateUserDto dto, UUID id) {
        Users user = repo.findById(id).orElseThrow(() -> new NotFoundException("user with this id not found"));

        if (dto.getFullName() != null && user.getFullName() != dto.getFullName()) {
            user.setFullName(dto.getFullName());
        }
        // phone number
        if (dto.getPhoneNumber() != null && user.getPhoneNumber() != dto.getPhoneNumber()) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        // description
        if (dto.getDescription() != null && user.getDescription() != dto.getDescription()) {
            user.setDescription(dto.getDescription());
        }

        // birthday
        if (dto.getBirthday() != null && user.getBirthday() != dto.getBirthday()) {
            user.setBirthday(dto.getBirthday());
        }

        if (dto.getProfilePicture() != null) {
            user.setProfilePicture(
                    replaceUserFile(
                            dto.getProfilePicture(),
                            user.getProfilePicture(),
                            USER_PROFILE_PIC_DIR));
        }

        if (dto.getBannerPicture() != null) {
            user.setBannerPicture(
                    replaceUserFile(
                            dto.getBannerPicture(),
                            user.getBannerPicture(),
                            USER_BANNER_PIC_DIR));
        }
        return user;
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public Users updateUsers(UpdateUserDto dto, CurrentUserContext curr, UUID target) {

        if (curr.role() != UserRoleType.ADMIN) {
            throw new ForbiddenAccessException("you don't have access to this feature");
        }

        Users user = repo.findById(target).orElseThrow(() -> new NotFoundException("user with this id not found"));

        if (dto.getFullName() != null && user.getFullName() != dto.getFullName()) {
            user.setFullName(dto.getFullName());
        }
        // phone number
        if (dto.getPhoneNumber() != null && user.getPhoneNumber() != dto.getPhoneNumber()) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        // description
        if (dto.getDescription() != null && user.getDescription() != dto.getDescription()) {
            user.setDescription(dto.getDescription());
        }

        // birthday
        if (dto.getBirthday() != null && user.getBirthday() != dto.getBirthday()) {
            user.setBirthday(dto.getBirthday());
        }

        if (dto.getRole() != null) {
            try {
                user.setRole(UserRoleType.valueOf(dto.getRole()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestsException("invalid user type, please only insert ADMIN, MODERATOR OR USER");
            }
        }

        if (dto.getProfilePicture() != null) {
            user.setProfilePicture(
                    replaceUserFile(
                            dto.getProfilePicture(),
                            user.getProfilePicture(),
                            USER_PROFILE_PIC_DIR));
        }

        if (dto.getBannerPicture() != null) {
            user.setBannerPicture(
                    replaceUserFile(
                            dto.getBannerPicture(),
                            user.getBannerPicture(),
                            USER_BANNER_PIC_DIR));
        }
        return user;
    }

    public Users getUsersProxy(UUID id) {
        return repo.getReferenceById(id);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUsers(UUID id) {
        Users user = repo.findById(id).orElseThrow(() -> new NotFoundException("user with this id not found"));
        user.setDeletedAt(LocalDateTime.now());
    }

    private void deleteUserFile(String relativePath) {
        if (relativePath == null) {
            return;
        }

        String[] path = relativePath
                .replaceFirst("^/", "")
                .split("/");

        try {
            storage.deletePublicFile(path);
        } catch (IOException e) {
            // ignore
        }
    }

    private String saveUserFile(
            MultipartFile file,
            String directory) {
        try {
            Path savedPath = storage.savePublicFile(
                    file,
                    USER_UPLOADS_DIR,
                    directory);

            return String.join(
                    "/",
                    "",
                    USER_UPLOADS_DIR,
                    directory,
                    savedPath.getFileName().toString());

        } catch (IOException e) {
            throw new BadRequestsException(e.getMessage());
        }
    }

    private String replaceUserFile(
            MultipartFile newFile,
            String oldFile,
            String directory) {
        try {
            boolean allowed = storage.validateWantedType(newFile, "image/");
            if (!allowed) {
                throw new BadRequestsException("invalid image format!");
            }

        } catch (IOException e) {
            throw new FatalError("something wrong while trying to save the user data");
        }

        if (!oldFile.contains("https")) {
            deleteUserFile(oldFile);
        }
        return saveUserFile(newFile, directory);
    }

}
