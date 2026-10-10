package com.sagun.blog_platform_backend.controller;

import com.sagun.blog_platform_backend.dto.CreatePostRequestDto;
import com.sagun.blog_platform_backend.dto.PageResponseDto;
import com.sagun.blog_platform_backend.dto.PostResponseDto;
import com.sagun.blog_platform_backend.dto.PostSummary;
import com.sagun.blog_platform_backend.entity.Post;
import com.sagun.blog_platform_backend.enums.PostStatus;
import com.sagun.blog_platform_backend.principal.UserPrincipal;
import com.sagun.blog_platform_backend.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class BlogController {

    private static final int MAX_PAGE_SIZE = 100;

    private final PostService service;

    @PostMapping("/create")
    public ResponseEntity<PostResponseDto> createPost(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody CreatePostRequestDto requestDto){
         PostResponseDto created = service.create(principal,requestDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/{slug}").buildAndExpand(created.slug()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping
    public PageResponseDto<PostSummary> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size){
        return service.listPublished(pageable(page,size));
    }

    @GetMapping("/slugs")
    public PageResponseDto<String> slugs(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "50") int size){
        return service.listPublishedSlugs(pageable(page,size));
    }

    @GetMapping("/{slug}")
    public PostResponseDto get(@PathVariable String slug){
        return service.getPublishedBySlug(slug);
    }

    @PostMapping("/{slug}/publish")
    public PostResponseDto publish(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable String slug){
        return service.publish(slug, userPrincipal);
    }

    private static Pageable pageable(int page, int size){
        int safePage = Math.max(page,0);
        int safeSize = Math.clamp(size, 1, MAX_PAGE_SIZE);
        return PageRequest.of(safePage,safeSize, Sort.by(Sort.Direction.DESC,"publishedAt").and(Sort.by(Sort.Direction.DESC,"id")));
    }


}
