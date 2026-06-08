package com.agmerrizky.cosplayin.posts.dto;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostsDto {

    private UUID replyTo;
    private UUID repostOf;
    private UUID quoteOf;

    @Size(max = 280)
    private String content;

    @Size(max = 4, message = "Maximum 4 files allowed")
    private MultipartFile[] media;
}
