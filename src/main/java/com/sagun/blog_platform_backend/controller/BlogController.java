package com.sagun.blog_platform_backend.controller;

import com.sagun.blog_platform_backend.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class BlogController {

    private final PostService service;

    @GetMapping("/")
    public String getAllPost(){
        return "My Blog post ";
    }

    @GetMapping("/{id}")
    public String getPostById(@PathVariable Long id){
        return "Post Id no: "+id;
    }

}
