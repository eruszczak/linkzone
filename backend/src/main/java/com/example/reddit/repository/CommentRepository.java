package com.example.reddit.repository;

import com.example.reddit.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByAccountUsername(String username, Pageable pageable);
    Page<Comment> findByPostIdAndParentIsNull(Long id, Pageable pageable);
    List<Comment> findByPostIdAndParentIsNull(Long id);
    Page<Comment> findByParentIdAndParentParentIsNull(Long id, Pageable pageable);
}
