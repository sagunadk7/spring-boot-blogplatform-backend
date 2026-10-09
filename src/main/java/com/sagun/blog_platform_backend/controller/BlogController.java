package com.sagun.blog_platform_backend.controller;

import com.sagun.blog_platform_backend.dto.CreatePostRequestDto;
import com.sagun.blog_platform_backend.dto.PostResponseDto;
import com.sagun.blog_platform_backend.entity.Post;
import com.sagun.blog_platform_backend.enums.PostStatus;
import com.sagun.blog_platform_backend.principal.UserPrincipal;
import com.sagun.blog_platform_backend.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/post")
public class BlogController {

    private final PostService service;

    @PostMapping("/create")
    public ResponseEntity<PostResponseDto> createPost(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody CreatePostRequestDto requestDto){
         PostResponseDto created = service.create(principal,requestDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{slug}").buildAndExpand(created.slug()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping("/{slug}")
    public Post getPostById(@PathVariable String  slug){
        return service.getBlogsBySlug(slug);
    }


}
