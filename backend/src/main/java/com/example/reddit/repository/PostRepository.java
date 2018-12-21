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
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT p.id as id, p.title as title, p.content as content, p.slug as slug, p.type as type, a.username as author, g.name as groupName, p.locked as locked, " +
            " (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id" +
            " WHERE p.id = :id",
            nativeQuery = true)
    Optional<IPostResponseDto> find(Long id, Long accountId);

    List<Post> findByGroupName(String name);

    @Query(value = "SELECT p.id as id, p.title as title, p.content as content, p.slug as slug, p.type as type, a.username as author, g.name as groupName, p.locked as locked, " +
            " (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id" +
            " WHERE g.name = :name" +
            " ORDER BY ?#{#pageable}",
            countQuery = "SELECT count(*) FROM posts p LEFT JOIN group_tbl g ON g.id = p.group_id WHERE g.name = :name",
            nativeQuery = true)
    Page<IPostResponseDto> findByGroupName(@Param("name") String name, @Param("pageable") Pageable pageable, @Param("accountId") Long accountId);

    @Query(value = "SELECT p.id as id, p.title as title, p.content as content, p.slug as slug, p.type as type, a.username as author, g.name as groupName, p.locked as locked, " +
            " (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id" +
            " WHERE a.username = :username" +
            " ORDER BY ?#{#pageable}",
            countQuery = "SELECT count(*) FROM posts p LEFT JOIN accounts a ON a.id = p.account_id WHERE a.username = :username",
            nativeQuery = true)
    Page<IPostResponseDto> findByAccountUsername(String username, Long accountId, Pageable pageable);

    // TODO countQuery
    // TODO countQuery
    // TODO countQuery
    @Query(value = "SELECT p.id as id, p.title as title, p.content as content, p.slug as slug, p.type as type, a.username as author, g.name as groupName, p.locked as locked, " +
            " (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " INNER JOIN group_membership gm ON gm.group_id = p.group_id AND gm.user_id = :accountId" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id" +
            " ORDER BY upvotedCount DESC, ?#{#pageable}",
            nativeQuery = true)
    Page<IPostResponseDto> findTop(@Param("accountId") Long accountId, @Param("pageable") Pageable pageable);

    @Query(value = "SELECT p.id as id, p.title as title, p.content as content, p.slug as slug, p.type as type, a.username as author, g.name as groupName, p.locked as locked, " +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " LEFT JOIN group_membership gm ON gm.group_id = p.group_id" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id AND g.is_default" +
            " ORDER BY upvotedCount DESC, ?#{#pageable}",
            nativeQuery = true)
    Page<IPostResponseDto> findTop(@Param("pageable") Pageable pageable);

    @Query(value = "SELECT p.id as id, p.title as title, p.content as content, p.slug as slug, p.type as type, a.username as author, g.name as groupName, p.locked as locked, " +
            " (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :accountId) as upvoted," +
            " (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :userId) as upvotedUser," +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id" +
            " WHERE p.account_id = :userId AND (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :userId)" +
            " ORDER BY upvotedCount DESC, ?#{#pageable}",
            nativeQuery = true)
    Page<IPostResponseDto> findUpvoted(Long userId, Long accountId, Pageable pageable);
}
