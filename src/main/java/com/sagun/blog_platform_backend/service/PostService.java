package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.customException.ConflictException;
import com.sagun.blog_platform_backend.customException.ResourceNotFoundException;
import com.sagun.blog_platform_backend.dto.*;
import com.sagun.blog_platform_backend.entity.Category;
import com.sagun.blog_platform_backend.entity.Post;
import com.sagun.blog_platform_backend.entity.User;
import com.sagun.blog_platform_backend.enums.CategoryType;
import com.sagun.blog_platform_backend.enums.PostStatus;
import com.sagun.blog_platform_backend.principal.UserPrincipal;
import com.sagun.blog_platform_backend.repository.CategoryRepository;
import com.sagun.blog_platform_backend.repository.PostRepository;
import com.sagun.blog_platform_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public PostResponseDto create(UserPrincipal principal,CreatePostRequestDto req) {
        User author = userRepository.getReferenceById(principal.getId());
        Post post = new Post(author, req.title().trim(), uniqueSlug(req.title()), req.content());
        if(req.categories() !=null && !req.categories().isEmpty()){
            post.getCategories().addAll(loadCategories(req.categories()));
        }
        return PostResponseDto.from(postRepository.saveAndFlush(post));
    }

    public PageResponseDto<PostSummary> listPublished(Pageable pageable){
        return PageResponseDto.from(postRepository.findByStatus(PostStatus.PUBLISHED,pageable).map(PostSummary::from));
    }


    public PageResponseDto<String> listPublishedSlugs(Pageable pageable){
        return PageResponseDto.from(postRepository.findPublishedSlugs(PostStatus.PUBLISHED,pageable));
    }

    public PostResponseDto getPublishedBySlug(String slug){
        Post post = postRepository.findBySlugAndStatus(slug, PostStatus.PUBLISHED).orElseThrow(() -> new ResourceNotFoundException("Post not found: " + slug));
        return PostResponseDto.from(post);
    }

    @Transactional
    public PostResponseDto update(String slug, UserPrincipal principal, UpdatePostRequestDto request){
        Post post = loadOwnedOrAdmin(slug, principal);
        if(post.getVersion() != request.version()){
            throw new ObjectOptimisticLockingFailureException(Post.class,post.getId());
        }
        post.edit(request.title().trim(),request.content());
        if(request.categoryTypes() != null){
            post.getCategories().clear();
            post.getCategories().addAll(loadCategories(request.categoryTypes()));
        }
        return PostResponseDto.from(postRepository.saveAndFlush(post));
    }

    @Transactional
    public PostResponseDto publish(String slug,UserPrincipal principal) {
        Post post = loadOwnedOrAdmin(slug, principal);
        post.publish();
        return PostResponseDto.from(postRepository.saveAndFlush(post));
    }

    @Transactional
    public PostResponseDto unpublish(String slug, UserPrincipal principal){
        Post post = loadOwnedOrAdmin(slug, principal);
        if(post.getStatus() != PostStatus.PUBLISHED){
            throw new ConflictException("Post id not published");
        }
        post.unpublish();
        return PostResponseDto.from(postRepository.saveAndFlush(post));
    }

    private Post loadOwnedOrAdmin(String slug, UserPrincipal principal){
        Post post = postRepository.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("Post not found: " + slug));
        boolean isOwner = post.getAuthor().getId().equals(principal.getId());
        boolean isAdmin = principal.getAuthorities().stream().anyMatch(roles -> Objects.equals(roles.getAuthority(), "ROLE_ADMIN"));
        if(!isOwner && !isAdmin){
            throw new AccessDeniedException("You cannot modify this post");
        }
        return post;
    }

    private Set<Category> loadCategories(Set<CategoryType> categoryTypes ){
        Set<Category> found = categoryRepository.findAllByTypeIn(categoryTypes);
        if(found.size() != categoryTypes.size()){
            throw new ResourceNotFoundException("One or more categories do not exist");
        }
        return found;
    }

    private String  uniqueSlug(String title){
        if(title == null || title.length() < 5 ) throw new IllegalArgumentException();
        String slug =  Normalizer.normalize(title, Normalizer.Form.NFD).replaceAll("\\p{M}","")
                .toLowerCase(Locale.ROOT).
                replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s","-")
                .replaceAll("-+","-");
        if(slug.isEmpty()) slug = "post";
        return postRepository.existsBySlug(slug)
                ? slug + "-" + UUID.randomUUID().toString().substring(0,8)
                : slug;
    }

}
