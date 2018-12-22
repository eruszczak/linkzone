package com.example.reddit.repository;

import com.example.reddit.model.CommentUpvote;
import com.example.reddit.model.PostUpvote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CommentUpvoteRepository extends JpaRepository<CommentUpvote, Long> {

    Optional<CommentUpvote> findByAccountIdAndCommentId(Long accountId, Long commentId);

    @Transactional
    void deleteByAccountIdAndCommentId(Long accountId, Long commentId);
}
