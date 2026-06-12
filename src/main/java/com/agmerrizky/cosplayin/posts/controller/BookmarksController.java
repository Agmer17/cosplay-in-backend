package com.agmerrizky.cosplayin.posts.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agmerrizky.cosplayin.common.anotations.CurrentUser;
import com.agmerrizky.cosplayin.common.anotations.RequireAuth;
import com.agmerrizky.cosplayin.common.api.SuccessResponse;
import com.agmerrizky.cosplayin.common.entity.Bookmarks;
import com.agmerrizky.cosplayin.common.type.CurrentUserContext;
import com.agmerrizky.cosplayin.posts.service.BookmarksService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RequestMapping("/api/bookmarks")
@RestController
@RequiredArgsConstructor
public class BookmarksController {

    private final BookmarksService service;

    @PostMapping("/create/{id}")
    @RequireAuth
    public ResponseEntity<SuccessResponse<Bookmarks>> handleCreateBookmarks(@PathVariable UUID id,
            @CurrentUser CurrentUserContext ctx) {
        Bookmarks bookmarks = service.createBookmarks(ctx.id(), id);

        return ResponseEntity.ok().body(
                SuccessResponse.<Bookmarks>builder()
                        .message("successfully creating a bookmarks")
                        .data(bookmarks)
                        .build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponse<Object>> handleDeleteBookmarks(@PathVariable UUID id,
            @CurrentUser CurrentUserContext ctx) {
        service.deleteBookmarks(ctx.id(), id);
        return ResponseEntity.ok().body(
                SuccessResponse.builder()
                        .data(null)
                        .message("successfully deleting the bookmarks")
                        .build());
    }

}
