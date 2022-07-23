package io.eryk.linkzone.repository;

import io.eryk.linkzone.dto.IPostResponseDto;
import io.eryk.linkzone.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    String QUERY = "SELECT p.id as id, p.title as title, p.content as content, p.slug as slug, p.type as type, a.username as author, g.name as groupName, g.logo as groupLogo, p.locked as locked, p.created_at as createdAt,";

    @Query(value = QUERY +
            " (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) as commentCount," +
            " (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id" +
            " WHERE p.id = :id",
            nativeQuery = true)
    Optional<IPostResponseDto> find(@Param("id") Long id, @Param("accountId") Long accountId);

    List<Post> findByGroupName(String name);

    @Query(value = QUERY +
            " (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) as commentCount," +
            " (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id" +
            " WHERE g.name = :name" +
            " ORDER BY p.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM posts p JOIN group_tbl g ON g.id = p.group_id WHERE g.name = :name",
            nativeQuery = true)
    Page<IPostResponseDto> findByGroupName(@Param("name") String name, Pageable pageable, @Param("accountId") Long accountId);

    @Query(value = QUERY +
            " (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) as commentCount," +
            " (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id" +
            " WHERE a.username = :username" +
            " ORDER BY p.created_at DESC",
            countQuery = "SELECT count(*) FROM posts p JOIN accounts a ON a.id = p.account_id WHERE a.username = :username",
            nativeQuery = true)
    Page<IPostResponseDto> findByAccountUsername(@Param("username") String username, @Param("accountId") Long accountId, Pageable pageable);

    @Query(value = QUERY +
            " (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) as commentCount," +
            " (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " INNER JOIN group_membership gm ON gm.group_id = p.group_id AND gm.user_id = :accountId" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id" +
            " ORDER BY upvotedCount DESC, p.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM posts p INNER JOIN group_membership gm ON gm.group_id = p.group_id AND gm.user_id = :accountId",
            nativeQuery = true)
    Page<IPostResponseDto> findTop(@Param("accountId") Long accountId, Pageable pageable);

    @Query(value = QUERY +
            " (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) as commentCount," +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id AND g.is_default" +
            " ORDER BY upvotedCount DESC, p.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM posts p JOIN group_tbl g ON g.id = p.group_id AND g.is_default",
            nativeQuery = true)
    Page<IPostResponseDto> findTop(Pageable pageable);

    @Query(value = QUERY +
            " (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) as commentCount," +
            " (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id" +
            " JOIN post_upvote pu ON pu.post_id = p.id AND pu.account_id = :userId" +
            " WHERE pu.is_upvote = 1" +
            " ORDER BY p.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM post_upvote pu WHERE pu.account_id = :userId AND pu.is_upvote = 1",
            nativeQuery = true)
    Page<IPostResponseDto> findUpvoted(@Param("userId") Long userId, @Param("accountId") Long accountId, Pageable pageable);
}
