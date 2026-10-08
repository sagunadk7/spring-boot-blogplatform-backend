package com.sagun.blog_platform_backend.controller;

import com.sagun.blog_platform_backend.dto.CreatePostRequestDto;
import com.sagun.blog_platform_backend.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/post")
public class BlogController {

    private final PostService service;

    @PostMapping("/create")
    public String createPost(@RequestBody CreatePostRequestDto requestDto){
        service.createBlog(requestDto);
        return "My Blog post ";
    }

    @GetMapping("/{id}")
    public String getPostById(@PathVariable Long id){
        return "Post Id no: "+id;
    }

}
