package com.sagun.blog_platform_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class BlogController {

    @GetMapping("/{id}")
    public String getAllPost(@PathVariable Long id){
        return "My Blog post: "+id;
    }

}
