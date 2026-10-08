package com.sagun.blog_platform_backend.dto;

import com.sagun.blog_platform_backend.enums.CategoryType;
import com.sagun.blog_platform_backend.enums.PostStatus;

import java.util.Set;

public record CreatePostRequestDto(String title, String content, Set<CategoryType> categories) { }
