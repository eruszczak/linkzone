package com.example.reddit.repository;

import com.example.reddit.model.Post;
import com.example.reddit.model.PostUpvote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostUpvoteRepository extends JpaRepository<PostUpvote, Long> {
}
