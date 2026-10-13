package com.sagun.blog_platform_backend.dto;

import java.time.Instant;

public record CommentResponseDto(Long id, String body, String author, Instant createdAt, Instant updatedAt, Long replyCount) {
}
