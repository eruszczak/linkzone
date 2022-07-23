package io.eryk.linkzone.repository;

import io.eryk.linkzone.dto.ICommentResponseDto;
import io.eryk.linkzone.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    String QUERY = "SELECT c.id, c.content, c.parent_id as parentId, p.id as postId, p.title as postTitle, p.slug as postSlug, g.name as groupName, c.created_at as createdAt, a.username, a.avatar,";

    @Query(value = QUERY +
            " (SELECT cu.is_upvote FROM comment_upvote cu WHERE cu.comment_id = c.id AND cu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(cu.is_upvote) FROM comment_upvote cu WHERE cu.comment_id = c.id) as upvotedCount" +
            " FROM comments c" +
            " JOIN posts p ON p.id=c.post_id" +
            " JOIN group_tbl g ON g.id=p.group_id" +
            " JOIN accounts a ON a.id=c.account_id" +
            " WHERE a.username = :username" +
            " ORDER BY c.created_at DESC",
            countQuery = "SELECT count(*) FROM comments c JOIN accounts a ON a.id = p.account_id WHERE a.username = :username",
            nativeQuery = true)
    Page<ICommentResponseDto> findByAccount(@Param("username") String username,  @Param("accountId") Long accountId, Pageable pageable);

    Page<Comment> findByPostIdAndParentIsNullOrderByCreatedAtDesc(Long id, Pageable pageable);

    @Query(value = QUERY +
            " (SELECT cu.is_upvote FROM comment_upvote cu WHERE cu.comment_id = c.id AND cu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(cu.is_upvote) FROM comment_upvote cu WHERE cu.comment_id = c.id) as upvotedCount" +
            " FROM comments c" +
            " JOIN posts p ON p.id=c.post_id" +
            " JOIN group_tbl g ON g.id=p.group_id" +
            " JOIN accounts a ON a.id=c.account_id" +
            " WHERE p.id = :id" +
            " ORDER BY IF(c.parent_id IS NULL, c.id, c.parent_id), c.id, c.created_at ASC",
            countQuery = "SELECT COUNT(*) FROM comments c JOIN posts p ON p.id=c.post_id WHERE p.id=:id",
            nativeQuery = true)
    Page<ICommentResponseDto> findByPostIdWithReplies(@Param("id") Long id,  @Param("accountId") Long accountId, Pageable pageable);

    @Query(value = QUERY +
            " (SELECT cu.is_upvote FROM comment_upvote cu WHERE cu.comment_id = c.id AND cu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(cu.is_upvote) FROM comment_upvote cu WHERE cu.comment_id = c.id) as upvotedCount" +
            " FROM comments c" +
            " JOIN posts p ON p.id=c.post_id" +
            " JOIN group_tbl g ON g.id=p.group_id" +
            " JOIN accounts a ON a.id=c.account_id" +
            " JOIN comment_upvote cu ON cu.comment_id = c.id AND cu.account_id = :userId" +
            " WHERE cu.is_upvote = 1" +
            " ORDER BY c.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM comment_upvote cu WHERE cu.account_id = :userId AND cu.is_upvote = 1",
            nativeQuery = true)
    Page<ICommentResponseDto> findUpvoted(@Param("userId") Long userId, @Param("accountId") Long accountId,  Pageable pageable);

    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtDesc(Long id);

    Page<Comment> findByParentIdAndParentParentIsNull(Long id, Pageable pageable);
}
