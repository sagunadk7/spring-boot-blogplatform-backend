package com.sagun.blog_platform_backend.controller;

import com.sagun.blog_platform_backend.dto.LikeResponseDto;
import com.sagun.blog_platform_backend.principal.UserPrincipal;
import com.sagun.blog_platform_backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts/{slug}/like")
public class LikeController {

    private final LikeService service;

    @GetMapping
    public LikeResponseDto status(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String slug){
        return service.status(slug,principal);
    }

    @PutMapping
    public LikeResponseDto like(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String slug){
        return service.like(slug,principal);
    }

    @DeleteMapping
    public LikeResponseDto unlike(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String slug){
        return service.unlike(slug,principal);
    }
}
