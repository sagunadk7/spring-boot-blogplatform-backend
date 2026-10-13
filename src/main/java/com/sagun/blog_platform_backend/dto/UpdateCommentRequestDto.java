package com.sagun.blog_platform_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommentRequestDto(@NotBlank @Size(max=2000) String body) {
}
