package com.sagun.blog_platform_backend.repository;

import com.sagun.blog_platform_backend.entity.Post;
import com.sagun.blog_platform_backend.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "author")
    Page<Post> findByStatus(PostStatus status);

    @EntityGraph(attributePaths = {"author","categories"})
    Optional<Post> findBySlug(String slug);

    @EntityGraph(attributePaths = "author")
    Page<Post> findByCategories_Slug(String slug, Pageable pageable);
}
