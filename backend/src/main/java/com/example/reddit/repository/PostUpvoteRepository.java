package com.example.reddit.repository;

import com.example.reddit.model.Post;
import com.example.reddit.model.PostUpvote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostUpvoteRepository extends JpaRepository<PostUpvote, Long> {

    Optional<PostUpvote> findByAccountIdAndPostId(Long accountId, Long postId);

    @Transactional
    void deleteByAccountIdAndPostId(Long accountId, Long postId);
}
