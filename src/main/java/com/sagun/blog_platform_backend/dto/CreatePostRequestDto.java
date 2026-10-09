package com.sagun.blog_platform_backend.dto;

import com.sagun.blog_platform_backend.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreatePostRequestDto(@NotBlank @Size(max = 200)String title, @NotBlank @Size(max = 50_000) String content, @Size(max = 10)Set< @NotNull CategoryType> categories) { }
