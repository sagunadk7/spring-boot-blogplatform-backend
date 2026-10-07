package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.customException.BlogDoesNotExistException;
import com.sagun.blog_platform_backend.entity.Post;
import com.sagun.blog_platform_backend.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PostService {

    private final PostRepository repository;

    public Post createBlog(Post post){
        return repository.save(post);

    }

    public Post getBlogsBySlug(String slug){
        return repository.findBySlug(slug).orElseThrow(()-> new BlogDoesNotExistException("Blog does not exist"));
    }

}
