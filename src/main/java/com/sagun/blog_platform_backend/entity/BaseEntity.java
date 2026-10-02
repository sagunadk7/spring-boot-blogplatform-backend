package com.sagun.blog_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name="created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name="updated_at",
            nullable = false
    )
    private Instant updateAt;

}
