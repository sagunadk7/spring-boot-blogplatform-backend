package com.sagun.blog_platform_backend.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponseDto<T>(List<T> content, int page,int size,long totalElements,int totalPages) {

    public static <T> PageResponseDto<T> from(Page<T> p){
        return new PageResponseDto<>(p.getContent(),p.getNumber(),p.getSize(),p.getTotalPages(),p.getTotalPages());
    }

}
