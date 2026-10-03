package com.sagun.blog_platform_backend.entity;


import com.sagun.blog_platform_backend.enums.PostStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "posts",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_posts_slug", columnNames = "slug"
        )
)
public class Post extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false,length = 220)
    private String slug;

    @Column(nullable = false,columnDefinition = "text")
    private String content;

    @Column(nullable = false,length = 20)
    private PostStatus status = PostStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id",nullable = false,foreignKey = @ForeignKey(name = "fk_posts_author"))
    private User author;

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    @OrderBy("createdAt ASC")
    @BatchSize(size = 50)
    private Set<Comment> comments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<PostLike> likes = new LinkedHashSet<>();






}
