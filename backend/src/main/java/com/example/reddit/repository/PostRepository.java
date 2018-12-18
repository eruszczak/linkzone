package com.example.reddit.repository;

import com.example.reddit.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByGroupName(String name);

    Page<Post> findByGroupName(String name, Pageable pageable);

    Page<Post> findByAccountUsername(String username, Pageable pageable);

    @Query("SELECT p FROM Post p INNER JOIN GroupMembership gm ON gm.group.id = p.group.id")
    Page<Post> findTop(String username, Pageable pageable);
}
