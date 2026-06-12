package com.agmerrizky.cosplayin.posts.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agmerrizky.cosplayin.common.entity.Bookmarks;
import com.agmerrizky.cosplayin.common.entity.Posts;
import com.agmerrizky.cosplayin.common.entity.Users;
import com.agmerrizky.cosplayin.common.exceptions.ConflictDataException;
import com.agmerrizky.cosplayin.common.exceptions.NotFoundException;
import com.agmerrizky.cosplayin.posts.repository.BookmarkRepository;
import com.agmerrizky.cosplayin.posts.repository.PostsRepository;
import com.agmerrizky.cosplayin.users.service.UsersService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarksService {
    private final BookmarkRepository bookmarkRepository;
    private final PostsRepository postsRepository;
    private final UsersService usersService;

    @Transactional
    public Bookmarks createBookmarks(UUID curr, UUID postsId) {

        Posts posts = postsRepository.findByIdAndDeletedAtIsNull(postsId).orElseThrow(
                () -> new NotFoundException("posts not found, you can't bookmarks the non existing posts"));

        try {
            Users userProxy = usersService.getUsersProxy(curr);
            Bookmarks bookmarks = Bookmarks.builder()
                    .user(userProxy)
                    .post(posts)
                    .build();

            bookmarkRepository.saveAndFlush(bookmarks);
            posts.setBookmarkCount(posts.getBookmarkCount() + 1);

            return bookmarks;

        } catch (DataIntegrityViolationException e) {
            throw new ConflictDataException(
                    "your account was not found in the database, maybe its has been deleted, contact the developer right now");
        } catch (EntityNotFoundException e) {
            throw new ConflictDataException(
                    "something conflict while trying to insert to a database maybe your account get deleted");
        }
    }

    @Transactional
    public void deleteBookmarks(UUID curr, UUID postId) {
        Posts posts = postsRepository.findByIdAndDeletedAtIsNull(postId).orElseThrow(
                () -> new NotFoundException("posts not found"));

        Bookmarks bookmarks = bookmarkRepository.findByUserIdAndPostId(curr, postId)
                .orElseThrow(() -> new NotFoundException("bookmark not found"));

        bookmarkRepository.delete(bookmarks);
        posts.setBookmarkCount(Math.max(0, posts.getBookmarkCount() - 1));
    }

    public List<Bookmarks> getBookmarksByUserId(UUID usersId) {
        List<Bookmarks> bookmarks = bookmarkRepository.findByUserId(usersId);

        return bookmarks;
    }

}
