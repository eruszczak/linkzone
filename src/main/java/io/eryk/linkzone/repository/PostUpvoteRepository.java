package io.eryk.linkzone.repository;

import io.eryk.linkzone.dto.ICounterDto;
import io.eryk.linkzone.model.PostUpvote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PostUpvoteRepository extends JpaRepository<PostUpvote, Long> {

    Optional<PostUpvote> findByAccountIdAndPostId(Long accountId, Long postId);

    @Transactional
    void deleteByAccountIdAndPostId(Long accountId, Long postId);

    @Query(value = "SELECT SUM(pu.is_upvote) as counter FROM post_upvote pu WHERE pu.post_id = :id", nativeQuery = true)
    ICounterDto getCounter(@Param("id") Long id);
}
