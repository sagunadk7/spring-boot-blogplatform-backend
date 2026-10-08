package com.sagun.blog_platform_backend.service;

import com.sagun.blog_platform_backend.customException.BlogDoesNotExistException;
import com.sagun.blog_platform_backend.dto.CreatePostRequestDto;
import com.sagun.blog_platform_backend.entity.Category;
import com.sagun.blog_platform_backend.entity.Post;
import com.sagun.blog_platform_backend.entity.User;
import com.sagun.blog_platform_backend.principal.UserPrincipal;
import com.sagun.blog_platform_backend.repository.CategoryRepository;
import com.sagun.blog_platform_backend.repository.PostRepository;
import com.sagun.blog_platform_backend.repository.UserRepository;
import com.sagun.blog_platform_backend.utils.SlugGenerator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public void createBlog(CreatePostRequestDto post){
        User authenticatedUser = getAuthenticatedUser();
        String slug = SlugGenerator.getSlug(post.title());
        Set<Category> categories = categoryRepository.findAllByTypeIn(post.categories());
        if(categories.size() != post.categories().size()){
            throw new RuntimeException("One or more categories do not exist");
        }
        Post post1 = new Post();
        post1.setTitle(post.title());
        post1.setContent(post.content());
        post1.setSlug(slug);
        post1.setAuthor(authenticatedUser);
        post1.setCategories(categories);
        postRepository.save(post1);
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

}
