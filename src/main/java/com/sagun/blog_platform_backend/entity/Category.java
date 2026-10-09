package com.sagun.blog_platform_backend.entity;


import com.sagun.blog_platform_backend.enums.CategoryType;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_categories_type", columnNames = "type"),
                @UniqueConstraint(name = "uk_categories_slug", columnNames = "slug")
        }
)
public class Category extends BaseEntity {

    @Column(nullable = false, length = 80)
    @Enumerated(EnumType.STRING)
    private CategoryType type;

    @Column(nullable = false, length = 100)
    private String slug;


    @ManyToMany(mappedBy = "categories")
    @BatchSize(size = 50)
    private Set<Post> posts = new HashSet<>();

    protected Category() { }

    public Category(CategoryType type, String slug) {
        this.type = type;
        this.slug = slug;
    }

    public CategoryType getType() { return type; }
    public String getSlug() { return slug; }
    public Set<Post> getPosts() { return posts; }



}
