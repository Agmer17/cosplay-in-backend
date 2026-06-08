package com.agmerrizky.cosplayin.posts.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.agmerrizky.cosplayin.common.entity.Posts;
import com.agmerrizky.cosplayin.common.entity.PostsMedia;
import com.agmerrizky.cosplayin.common.exceptions.BadRequestsException;
import com.agmerrizky.cosplayin.common.exceptions.ConflictDataException;
import com.agmerrizky.cosplayin.common.exceptions.FatalError;
import com.agmerrizky.cosplayin.common.type.CurrentUserContext;
import com.agmerrizky.cosplayin.common.type.MediaType;
import com.agmerrizky.cosplayin.common.type.PostType;
import com.agmerrizky.cosplayin.common.type.PostsVisibility;
import com.agmerrizky.cosplayin.posts.dto.CreatePostsDto;
import com.agmerrizky.cosplayin.posts.repository.PostsRepository;
import com.agmerrizky.cosplayin.users.service.UsersService;
import com.agmerrizky.cosplayin.utils.ServerStorage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostsService {

    private final PostsRepository postsRepo;
    private final ServerStorage storage;
    private final String PUBLIC_POSTS_DIR = "posts";
    private final UsersService usersService;

    public Posts createPosts(CreatePostsDto dto, CurrentUserContext curr) {
        int referenceCount = 0;
        if (dto.getReplyTo() != null)
            referenceCount++;
        if (dto.getRepostOf() != null)
            referenceCount++;
        if (dto.getQuoteOf() != null)
            referenceCount++;

        // 1. Validasi tidak boleh double reference
        if (referenceCount > 1) {
            throw new BadRequestsException("Post cannot be a reply, repost, and quote at the same time");
        }

        // 2. Tentukan Tipe Postingan
        PostType postType = PostType.ORIGINAL; // Pastikan ada enum value (misal REGULAR/NORMAL)
        if (dto.getReplyTo() != null)
            postType = PostType.REPLY;
        else if (dto.getRepostOf() != null)
            postType = PostType.REPOST;
        else if (dto.getQuoteOf() != null)
            postType = PostType.QUOTE;

        // 3. Validasi Konten & Media Berdasarkan Tipe
        if (postType == PostType.REPOST) {
            // Repost dilarang keras punya media atau konten sendiri
            if (dto.getMedia() != null && dto.getMedia().length > 0) {
                throw new BadRequestsException("Reposts cannot contain media!");
            }
            if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
                throw new BadRequestsException("Reposts cannot contain content! Use Quote instead.");
            }
        } else {
            // Validasi normal untuk Regular, Reply, dan Quote
            boolean hasContent = dto.getContent() != null && !dto.getContent().trim().isEmpty();
            boolean hasMedia = dto.getMedia() != null && dto.getMedia().length > 0;

            if (!hasContent && !hasMedia) {
                throw new BadRequestsException(
                        "If the content is empty you need to provide media, you cannot post blank text with no content.");
            }
        }

        Posts posts = Posts.builder()
                .content(postType == PostType.REPOST ? null : dto.getContent())
                .postType(postType)
                .visibility(PostsVisibility.PUBLIC)
                .user(usersService.getUsersProxy(curr.id()))
                .build();

        if (postType == PostType.REPLY) {
            posts.setReplyTo(postsRepo.getReferenceById(dto.getReplyTo()));
        } else if (postType == PostType.REPOST) {
            posts.setRepostOf(postsRepo.getReferenceById(dto.getRepostOf()));
        } else if (postType == PostType.QUOTE) {
            posts.setQuoteOf(postsRepo.getReferenceById(dto.getQuoteOf()));
        }

        // 6. Handle Media (Dilewati jika ini adalah Repost)
        if (postType != PostType.REPOST && dto.getMedia() != null && dto.getMedia().length > 0) {

            if (dto.getMedia().length > 4) {
                throw new BadRequestsException("The maximum media you can upload is 4!");
            }

            try {
                List<Path> results = storage.savePublicFiles(dto.getMedia(), PUBLIC_POSTS_DIR);
                List<PostsMedia> postsMediaResults = new ArrayList<>(results.size());

                for (int i = 0; i < results.size(); i++) {
                    Path p = results.get(i);
                    String filename = p.getFileName().toString();
                    MediaType mdType = ServerStorage.getMediaTypeFromFilename(filename);

                    String mdUrl = String.join("/", "", PUBLIC_POSTS_DIR, filename);

                    // TODO: Validate the media type and generate metadata
                    PostsMedia md = PostsMedia.builder()
                            .post(posts)
                            .mediaUrl(mdUrl)
                            .mediaType(mdType)
                            .displayOrder(i)
                            .build();

                    postsMediaResults.add(md);
                }

                posts.setMedia(postsMediaResults);
            } catch (IOException e) {
                System.out.println("ERROR : " + e.getMessage());
                throw new FatalError(
                        "Something went wrong while trying to create a post, please try again another time.");
            }
        }

        try {
            postsRepo.save(posts);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictDataException("user data integrity is violated : " + e.getMessage());
        }
        return posts;
    }
}
