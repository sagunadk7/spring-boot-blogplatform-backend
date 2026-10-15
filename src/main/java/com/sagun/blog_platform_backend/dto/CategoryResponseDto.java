package com.sagun.blog_platform_backend.dto;


import com.sagun.blog_platform_backend.entity.Category;
import com.sagun.blog_platform_backend.enums.CategoryType;

public record CategoryResponseDto(Long id, CategoryType type, String slug) {

    public static CategoryResponseDto from(Category c){
        return new CategoryResponseDto(c.getId(),c.getType(), c.getSlug());
    }

}
