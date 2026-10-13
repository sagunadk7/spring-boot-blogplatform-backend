package com.sagun.blog_platform_backend.repository;

import com.sagun.blog_platform_backend.dto.CommentResponseDto;
import com.sagun.blog_platform_backend.entity.Comment;
import com.sagun.blog_platform_backend.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository  extends JpaRepository<Comment, Long> {

    @Query(value = """
            select new com.sagun.blog_platform_backend.dto.CommentResponseDto(
                        c.id,c.body,a.username,c.createdAt,c.updatedAt,(select count(r) from Comment r where r.parent= c)
                        ) from Comment c join c.author a 
                                    where c.post.id = :postId and c.parent is null
                                                order by c.createdAt asc, c.id asc
            """, countQuery = "select count(c) from Comment c where c.post.id = :postId and c.parent is null"
    )
    Page<CommentResponseDto> findTopLevel(@Param("postId")Long postId, Pageable pageable);

    @Query(value = """
    select new com.sagun.blog_platform_backend.dto.CommentResponseDto(c.id,c.body,a.username,c.createdAt, c.updatedAt,0L)
    from Comment c join c.author a where c.parent.id = :parentId and c.post.status = :status
    order by c.createdAt asc, c.id asc
""",countQuery = "select count(c) from Comment c where c.parent.id = :parentId and c.post.status = :status")
    Page<CommentResponseDto> findReplies(Long parentId, PostStatus status, Pageable pageable);

    long countByParentId(Long parentId);
}
