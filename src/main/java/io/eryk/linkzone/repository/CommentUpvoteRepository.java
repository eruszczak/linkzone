package io.eryk.linkzone.repository;

import io.eryk.linkzone.dto.ICounterDto;
import io.eryk.linkzone.model.CommentUpvote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CommentUpvoteRepository extends JpaRepository<CommentUpvote, Long> {

    Optional<CommentUpvote> findByAccountIdAndCommentId(Long accountId, Long commentId);

    @Transactional
    void deleteByAccountIdAndCommentId(Long accountId, Long commentId);

    @Query(value = "SELECT SUM(cu.is_upvote) as counter FROM comment_upvote cu WHERE cu.comment_id = :id", nativeQuery = true)
    ICounterDto getCounter(@Param("id") Long id);
}
