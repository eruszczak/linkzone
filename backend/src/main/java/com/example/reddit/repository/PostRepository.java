package com.example.reddit.repository;

import com.example.reddit.dto.IPostResponseDto;
import com.example.reddit.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.websocket.server.PathParam;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByGroupName(String name);

    Page<Post> findByGroupName(String name, Pageable pageable);

    @Query(value = "SELECT p.id as id, (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :accountId) as upvoted FROM posts p LEFT JOIN `group` g ON g.id = p.group_id WHERE g.name = :name ORDER BY ?#{#pageable}",
            countQuery = "SELECT count(*) FROM posts p LEFT JOIN `group` g ON g.id = p.group_id WHERE g.name = :name",
            nativeQuery = true)
    Page<IPostResponseDto> findByGroupName(@Param("name") String name, @Param("pageable") Pageable pageable, @Param("accountId") Long accountId);

    Page<Post> findByAccountUsername(String username, Pageable pageable);

    @Query("SELECT p FROM Post p INNER JOIN GroupMembership gm ON gm.group.id = p.group.id")
    Page<Post> findTop(String username, Pageable pageable);

    @Query(value = "SELECT p.id as id FROM posts p LEFT JOIN `group` g ON g.id = p.group_id INNER JOIN post_upvote pu ON pu.post_id = p.id WHERE pu.is_upvote AND p.account_id = :accountId", nativeQuery = true)
    Page<IPostResponseDto> findUpvoted(Long accountId, Pageable pageable);
}
