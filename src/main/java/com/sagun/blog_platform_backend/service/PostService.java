package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PostService {

    private final PostRepository repository;

    public String getBlogsBySlug(){
        return repository.findB
    }

}
