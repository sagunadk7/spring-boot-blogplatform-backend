package com.sagun.blog_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name="comment")
@Getter
@Setter
@NoArgsConstructor
public class Comment extends BaseEntity {

    @Column(nullable = false, length = 2000)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="post_id",nullable = false,foreignKey = @ForeignKey(name = "fk_comments_post"))
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "author_id",nullable = false,foreignKey = @ForeignKey(name = "fk_comments_author"))
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id",foreignKey = @ForeignKey(name = "fk_comments_parents"))
    private Comment  parent;

    @OneToMany(mappedBy = "parent",cascade = CascadeType.ALL,orphanRemoval = true)
    @BatchSize(size = 50)
    private Set<Comment> replies = new LinkedHashSet<>();


    public Comment(Post post, User author, String body, Comment parent) {
        this.post = post;
        this.author = author;
        this.body = body;
        this.parent = parent;
    }

    public void edit(String body){
        this.body = body;
    }

    public Comment reply(User author, String body){
        Comment r = new Comment(this.post, author, body, this);
        replies.add(r);
        post.getComments().add(r);
        return r;
    }
}
