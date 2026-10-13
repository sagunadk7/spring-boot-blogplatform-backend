package com.sagun.blog_platform_backend.repository;

import com.sagun.blog_platform_backend.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    long countByPostId(Long postId);


    long deleteByUserIdAndPost(Long userId, Long postId);

    @Modifying
    @Query(value = """
            insert into post_likes(user_Id, post_id,created_at, updated_at, version)
            values(:userId, :postId, now(),now(),0)
            on conflict(user_id, post_id) do nothing
               """, nativeQuery = true
    )
    int insertIfAbsent(@Param("userId") Long userId, @Param("postId") Long postId);

    @Modifying
    @Query("delete from PostLike l where l.user.id = :userId and l.post.id = :postId")
    int deleteLike(@Param("userId")Long userId, @Param("postId") Long postId);

}
