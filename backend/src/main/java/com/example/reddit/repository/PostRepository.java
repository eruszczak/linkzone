package com.example.reddit.repository;

import com.example.reddit.dto.IPostResponseDto;
import com.example.reddit.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.websocket.server.PathParam;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByGroupName(String name);

//    @Query(value = "SELECT p.* as post, EXISTS(SELECT COUNT(*) FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = p.account_id) as upvoted FROM posts p WHERE g.name = :name LEFT JOIN groups g ON groups.id = p.group_id", nativeQuery = true)
    @Query(value = "SELECT p.id as id, (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = p.account_id) as upvoted FROM posts p LEFT JOIN `group` g ON g.id = p.group_id WHERE g.name = :name", nativeQuery = true)
//    @Query(value = "SELECT new com.example.reddit.dto.PostUpvoteDb(p) FROM Post p WHERE p.group.name = :name")
//    @Query(value = "SELECT new com.example.reddit.dto.PostUpvoteDb(p, EXISTS(SELECT COUNT(*) FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = p.account_id)) FROM Post p WHERE p.group.name = :name")
    Page<IPostResponseDto> findByGroupName(@PathParam("name") String name, Pageable pageable);

    Page<Post> findByAccountUsername(String username, Pageable pageable);

    @Query("SELECT p FROM Post p INNER JOIN GroupMembership gm ON gm.group.id = p.group.id")
    Page<Post> findTop(String username, Pageable pageable);
}
