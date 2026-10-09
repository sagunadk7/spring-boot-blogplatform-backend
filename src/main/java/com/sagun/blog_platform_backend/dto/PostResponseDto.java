package com.sagun.blog_platform_backend.dto;

import com.sagun.blog_platform_backend.entity.Category;
import com.sagun.blog_platform_backend.entity.Post;
import com.sagun.blog_platform_backend.enums.PostStatus;

import java.time.Instant;
import java.util.List;

public record PostResponseDto(
        Long id,
        String title,
        String slug,
        String content,
        PostStatus status,
        String author,
        List<String> categories,
        Instant publishedAt,
        Instant createdAt
) {

    public static PostResponseDto from(Post post){
        return new PostResponseDto(
                post.getId(),post.getTitle(),post.getSlug(),post.getContent(),post.getStatus(), post.getAuthor().getUsername(),post.getCategories().stream().map(c->c.getType().toString()).sorted().toList(),post.getPublishedAt(),post.getCreatedAt()
        );
    }

}
