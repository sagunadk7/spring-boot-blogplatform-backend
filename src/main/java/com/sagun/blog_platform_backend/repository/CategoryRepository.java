package com.sagun.blog_platform_backend.repository;

import com.sagun.blog_platform_backend.entity.Category;
import com.sagun.blog_platform_backend.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByType(CategoryType type);

    boolean existsByType(CategoryType type);
    Set<Category> findAllByTypeIn(Set<CategoryType> types);
}
