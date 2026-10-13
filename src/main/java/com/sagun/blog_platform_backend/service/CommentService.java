package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.customException.InvalidRequestException;
import com.sagun.blog_platform_backend.customException.ResourceNotFoundException;
import com.sagun.blog_platform_backend.dto.CommentResponseDto;
import com.sagun.blog_platform_backend.dto.CreateCommentRequestDto;
import com.sagun.blog_platform_backend.dto.PageResponseDto;
import com.sagun.blog_platform_backend.dto.UpdateCommentRequestDto;
import com.sagun.blog_platform_backend.entity.Comment;
import com.sagun.blog_platform_backend.enums.PostStatus;
import com.sagun.blog_platform_backend.principal.UserPrincipal;
import com.sagun.blog_platform_backend.repository.CommentRepository;
import com.sagun.blog_platform_backend.repository.PostRepository;
import com.sagun.blog_platform_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public PageResponseDto<CommentResponseDto> listForPost(String slug, Pageable pageable){
        Long postId = publishedPostId(slug);
        return PageResponseDto.from(commentRepository.findTopLevel(postId, pageable));
    }

    public PageResponseDto<CommentResponseDto>listReplies(Long commentId, Pageable pageable){
        if(!commentRepository.existsById(commentId)){
            throw new ResourceNotFoundException("Comment not found: "+commentId);
        }
        return PageResponseDto.from(commentRepository.findReplies(commentId,PostStatus.PUBLISHED,pageable));
    }

    @Transactional
    public CommentResponseDto update(Long id, UserPrincipal principal, UpdateCommentRequestDto requestDto){
        Comment comment = find(id);
        if(!comment.getAuthor().getId().equals(principal.getId())){
            throw new AccessDeniedException("You can only edit your own comments");
        }
        comment.edit(requestDto.body().trim());
        comment = commentRepository.saveAndFlush(comment);
        return new CommentResponseDto(comment.getId(), comment.getBody(), comment.getAuthor().getUsername(),
                comment.getCreatedAt(),comment.getUpdatedAt(),commentRepository.countByParentId(id));
    }

    @Transactional
    public CommentResponseDto create(String slug, UserPrincipal principal, CreateCommentRequestDto dto){
        Long postId = publishedPostId(slug);
        Comment parent  = null;
        if(dto.parentId() != null){
            parent = commentRepository.findById(dto.parentId()).orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
            if(!parent.getPost().getId().equals(postId)){
                throw new InvalidRequestException("Parent comment belongs to a different post");
            }
            if(parent.getParent() != null){
                throw new InvalidRequestException("Replies to a replies are not supported");
            }
        }
        Comment comment = new Comment(postRepository.getReferenceById(postId), userRepository.getReferenceById(principal.getId()), dto.body().trim(), parent);
        comment = commentRepository.saveAndFlush(comment);
        return new CommentResponseDto(comment.getId(), comment.getBody(), principal.getUsername(),comment.getCreatedAt(),comment.getUpdatedAt(),0L);
    }

    @Transactional
    public void delete(Long id, UserPrincipal principal){
        Comment comment = find(id);
        boolean isAuthor = comment.getAuthor().getId().equals(principal.getId());
        boolean isStaff = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN)") || a.getAuthority().equals("ROLE_MODERATOR"));
        if(!isStaff && !isAuthor){
            throw new AccessDeniedException("You cannot delete this comment");
        }
        commentRepository.delete(comment);
    }

    private Comment find(Long id){
        return commentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Comment not found: "+id));
    }

    private Long publishedPostId(String slug){
        return postRepository.findIdBySlugAndStatus(slug, PostStatus.PUBLISHED).orElseThrow(()->new ResourceNotFoundException("Post not found: "+slug));
    }

}
