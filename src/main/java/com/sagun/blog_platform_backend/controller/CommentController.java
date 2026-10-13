package com.sagun.blog_platform_backend.controller;

import com.sagun.blog_platform_backend.dto.CommentResponseDto;
import com.sagun.blog_platform_backend.dto.CreateCommentRequestDto;
import com.sagun.blog_platform_backend.dto.PageResponseDto;
import com.sagun.blog_platform_backend.dto.UpdateCommentRequestDto;
import com.sagun.blog_platform_backend.principal.UserPrincipal;
import com.sagun.blog_platform_backend.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private static final int MAX_PAGE_SIZE = 100;
    private final CommentService service;

    @GetMapping("/posts/{slug}comments")
    public PageResponseDto<CommentResponseDto> list(@PathVariable String slug,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size){
        return service.listForPost(slug,pageable(page,size));

    }

    @PostMapping("/posts/{slug}/comments")
    public ResponseEntity<CommentResponseDto> create(@AuthenticationPrincipal UserPrincipal principal,
                                                     @PathVariable String slug,
                                                     @Valid @RequestBody CreateCommentRequestDto request){
        CommentResponseDto created = service.create(slug, principal, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/comments/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);

    }

    @GetMapping("/comments/{id}/replies")
    public PageResponseDto<CommentResponseDto> replies(@PathVariable Long id,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size){
        return service.listReplies(id,pageable(page,size));
    }

    @PutMapping("/comments/{id}")
    public CommentResponseDto update(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal, UpdateCommentRequestDto requestDto){
        return service.update(id,userPrincipal,requestDto);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id){
        service.delete(id,principal);
        return ResponseEntity.noContent().build();
    }

    private static Pageable pageable(int page, int size){
        return PageRequest.of(
                Math.max(page,0),
                Math.clamp(size,0,MAX_PAGE_SIZE)
        );
    }

}
