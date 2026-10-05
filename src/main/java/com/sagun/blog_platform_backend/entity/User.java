package com.sagun.blog_platform_backend.entity;

import com.sagun.blog_platform_backend.enums.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Entity
@Table(name = "users")
public class  User extends BaseEntity {

    @Column(nullable = false,length = 50)
    private String username;

    @Column(nullable=false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_roles",joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role",nullable = false,length = 20)
    private Set<Role> roles = new HashSet<>(Set.of(Role.USER));

    @OneToMany(mappedBy = "author")
    @BatchSize(size = 50)
    public Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "author")
    @BatchSize(size = 50)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @BatchSize(size = 50)
    private Set<PostLike> likes = new HashSet<>();

    public User(){}

    public User(String username,String email, String passwordHash){
        this.username = username;
        setEmail(email);
        this.passwordHash = passwordHash;
    }

    public void setEmail(String email){
        this.email = email.toLowerCase(Locale.ROOT).trim();
    }

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }

    public String getPasswordHash(){
        return passwordHash;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Set<PostLike> getLikes() {
        return likes;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public void setLikes(Set<PostLike> likes) {
        this.likes = likes;
    }
}
