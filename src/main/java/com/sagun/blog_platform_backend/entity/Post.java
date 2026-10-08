package com.sagun.blog_platform_backend.entity;


import com.sagun.blog_platform_backend.enums.PostStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
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
    @Enumerated(EnumType.STRING)
    private PostStatus status = PostStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id",nullable = false,foreignKey = @ForeignKey(name = "fk_posts_author"))
    private User author;

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    @OrderBy("createdAt ASC")
    @BatchSize(size = 50)
    private Set<Comment> comments = new LinkedHashSet<>();

    @Column(name="published_at")
    private Instant publishedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_categories",
            joinColumns = @JoinColumn(name = "post_id",foreignKey = @ForeignKey(name="fk_pc_post")),
            inverseJoinColumns = @JoinColumn(name="category_id",foreignKey = @ForeignKey(name = "fk_pc_category"))

    )
    @BatchSize(size = 50)
    private Set<Category> categories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post")
    @BatchSize(size = 50)
    private Set<PostLike> likes = new LinkedHashSet<>();

    public Post(User author, String title, String slug, String content){
        this.author = author;
        this.title = title;
        this.slug = slug;
        this.content = content;
    }

    public void edit(String title, String content){
        this.title = title;
        this.content = content;
    }

    public void publish(){
        this.status = PostStatus.PUBLISHED;
        if(this.publishedAt == null) this.publishedAt = Instant.now();
    }

    public void addCategory(Category category){
        categories.add(category);
        category.getPosts().add(this);
    }

    public void removeCategory(Category category){
        categories.remove(category);
        category.getPosts().remove(this);
    }

    public Comment addComment(User author, String body){
        Comment c = new Comment(this, author, body, null);
        comments.add(c);
        return c;
    }


    public void removeComment(Comment comment){
        comments.remove(comment);
    }




}
