package com.agmerrizky.cosplayin.posts.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agmerrizky.cosplayin.common.entity.Posts;
import com.agmerrizky.cosplayin.common.entity.PostsLikes;
import com.agmerrizky.cosplayin.common.entity.Users;
import com.agmerrizky.cosplayin.common.exceptions.ConflictDataException;
import com.agmerrizky.cosplayin.common.exceptions.NotFoundException;
import com.agmerrizky.cosplayin.posts.repository.PostsLikesRepository;
import com.agmerrizky.cosplayin.posts.repository.PostsRepository;
import com.agmerrizky.cosplayin.users.service.UsersService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final PostsLikesRepository repo;
    private final PostsRepository postsRepo;
    private final UsersService userService;

    public Set<UUID> findLikedPosts(UUID curr, List<UUID> postsId) {
        Set<UUID> result = repo.findLikedPostIds(curr, postsId);

        return result;

    }

    public Set<UUID> findLikedPosts(UUID curr, UUID postsId) {
        Set<UUID> result = repo.findLikedPostIds(curr, List.of(postsId));
        return result;

    }

    @Transactional
    public void createLikes(UUID postsId, UUID curr) {
        Posts posts = postsRepo.findByIdAndDeletedAtIsNull(postsId)
                .orElseThrow(() -> new NotFoundException("posts not found, you can't like unavaible posts"));
        try {
            Users user = userService.getUsersProxy(curr);
            PostsLikes likes = PostsLikes.builder()
                    .post(posts)
                    .user(user)
                    .build();

            repo.saveAndFlush(likes);
            posts.setLikeCount(posts.getLikeCount() + 1);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictDataException(
                    "something conflict while trying to insert to a database, either your account get deleted or you've already likes this posts");
        } catch (EntityNotFoundException e) {
            throw new ConflictDataException(
                    "something conflict while trying to insert to a database, either your account get deleted or you've already likes this posts");
        }
    }

    @Transactional
    public void unlikePost(UUID userId, UUID postId) {
        Posts posts = postsRepo.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new NotFoundException("posts not found, you can't like unavaible posts"));
        try {
            int res = repo.deleteLike(userId, postId);
            if (res == 0) {
                throw new NotFoundException("likes in this posts was not found, can't remove non existing likes");
            }

            posts.setLikeCount(posts.getLikeCount() - 1);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictDataException(
                    "something conflict while trying to insert to a database, either your account get deleted or you've already likes this posts");
        } catch (EntityNotFoundException e) {
            throw new ConflictDataException(
                    "something conflict while trying to insert to a database, either your account get deleted or you've already likes this posts");
        }
    }

}
