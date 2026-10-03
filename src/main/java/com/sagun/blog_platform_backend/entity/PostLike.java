package com.sagun.blog_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "post_likes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_likes_user_post",columnNames = {"user_id","post_id"}
        )
)
public class PostLike extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id",nullable = false,foreignKey = @ForeignKey(name = "fk_post_likes_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="post_id",nullable = false,foreignKey = @ForeignKey(name="fk_post_likes_post"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;





}
