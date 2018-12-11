package com.example.reddit.repository;

import com.example.reddit.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByGroupName(String name);

    Page<Post> findByGroupName(String name, Pageable pageable);

    Page<Post> findByAccountUsername(String username, Pageable pageable);
}
