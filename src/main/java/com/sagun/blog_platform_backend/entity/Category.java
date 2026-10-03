package com.sagun.blog_platform_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Category extends BaseEntity{

    @Column(nullable = false,length = 80)
    private String name;

    @Column(nullable = false, length = 100)
    private String slug;

    @OneToMany(mappedBy = "categories")
    @BatchSize(size = 50)
    private Set<Post> posts = new LinkedHashSet<>();


}
