package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.customException.ResourceNotFoundException;
import com.sagun.blog_platform_backend.dto.LikeResponseDto;
import com.sagun.blog_platform_backend.enums.PostStatus;
import com.sagun.blog_platform_backend.principal.UserPrincipal;
import com.sagun.blog_platform_backend.repository.PostLikeRepository;
import com.sagun.blog_platform_backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public LikeResponseDto status(String slug, UserPrincipal principal){
        Long postId = publishedPostId(slug);
        boolean liked = principal != null && postLikeRepository.existsByUserIdAndPostId(principal.getId(), postId);
        return new LikeResponseDto(postLikeRepository.countByPostId(postId),liked);
    }

    @Transactional
    public LikeResponseDto like(String slug, UserPrincipal principal){
        Long postId = publishedPostId(slug);
        postLikeRepository.insertIfAbsent(principal.getId(), postId);
        return new LikeResponseDto(postLikeRepository.countByPostId(postId),true);
    }

    @Transactional
    public LikeResponseDto unlike(String slug, UserPrincipal me){
        Long postId = publishedPostId(slug);
        postLikeRepository.deleteLike(me.getId(),postId);
        return new LikeResponseDto(postLikeRepository.countByPostId(postId),false);
    }

    private Long publishedPostId(String slug){
        return postRepository.findIdBySlugAndStatus(slug, PostStatus.PUBLISHED).orElseThrow(()->new ResourceNotFoundException("Post not found: "+slug));
    }




}
