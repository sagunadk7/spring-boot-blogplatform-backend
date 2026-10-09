package com.sagun.blog_platform_backend.dto;

import com.sagun.blog_platform_backend.entity.Post;

import java.time.Instant;

public record PostSummary (
        Long id,
        String title,
        String slug,
        String author,
        Instant publishedAt
){

    public static PostSummary from (Post post){
        return new PostSummary(post.getId(),post.getTitle(),post.getSlug(),post.getAuthor().getUsername(),post.getPublishedAt());
    }

}
