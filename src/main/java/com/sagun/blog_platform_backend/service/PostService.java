package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.customException.BlogDoesNotExistException;
import com.sagun.blog_platform_backend.customException.ResourceNotFoundException;
import com.sagun.blog_platform_backend.dto.CreatePostRequestDto;
import com.sagun.blog_platform_backend.dto.PostResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
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
    public PostResponseDto create(UserPrincipal principal,CreatePostRequestDto req) throws ResourceNotFoundException {
        User author = userRepository.getReferenceById(principal.getId());
        Post post = new Post(author, req.title().trim(), uniqueSlug(req.title()), req.content());
        if(req.categories() != null && !req.categories().isEmpty()){
            Set<CategoryType> categoryTypes = req.categories();
            Set<Category> found = categoryRepository.findAllByTypeIn(categoryTypes);
            if(found.size() != categoryTypes.size()){
                throw new ResourceNotFoundException("One or more categories do not exists");
            }
            post.getCategories().addAll(found);
        }
        postRepository.save(post);
        return PostResponseDto.from(post);
    }

    private User getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)){
            throw new RuntimeException("No authenticated author in context");
        }
        return userRepository.findByUsername(principal.getUsername()).orElseThrow(()->new UsernameNotFoundException("Authenticated author no longer exists"));
    }

    public Post getBlogsBySlug(String slug){
        return postRepository.findBySlug(slug).orElseThrow(()-> new BlogDoesNotExistException("Blog does not exist"));
    }

    public Page<String> getAllSlugOfBlog(PostStatus status, Pageable pageable){
        return postRepository.findPublishedSlugs(status,pageable);
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
