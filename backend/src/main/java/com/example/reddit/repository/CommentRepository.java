package com.example.reddit.repository;

import com.example.reddit.dto.ICommentResponseDto;
import com.example.reddit.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByAccountUsername(String username, Pageable pageable);

    Page<Comment> findByPostIdAndParentIsNullOrderByCreatedAtDesc(Long id, Pageable pageable);

    @Query(value = "SELECT c.id, c.content, c.parent_id as parentId, p.id as postId, p.title as postTitle, g.name as groupName, c.created_at as createdAt, a.username, a.avatar," +
            " (SELECT cu.is_upvote FROM comment_upvote cu WHERE cu.comment_id = c.id AND cu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(cu.is_upvote) FROM comment_upvote cu WHERE cu.comment_id = c.id) as upvotedCount" +
            " FROM comments c" +
            " JOIN posts p ON p.id=c.post_id" +
            " JOIN group_tbl g ON g.id=p.group_id" +
            " JOIN accounts a ON a.id=c.account_id" +
            " WHERE p.id = :id" +
            " ORDER BY IF(c.parent_id IS NULL, c.id, c.parent_id), c.id, c.created_at ASC, ?#{#pageable}",
            countQuery = "SELECT COUNT(*) FROM comments c JOIN posts p ON p.id=c.post_id WHERE p.id=:id",
            nativeQuery = true)
    Page<ICommentResponseDto> findByPostIdWithReplies(Long id, Long accountId, Pageable pageable);

    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtDesc(Long id);

    Page<Comment> findByParentIdAndParentParentIsNull(Long id, Pageable pageable);
}
