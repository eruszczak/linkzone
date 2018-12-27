package com.example.reddit.dto;

import com.example.reddit.controller.post.PostType;

import java.time.Instant;

public interface IAccountStatsDto {
    Long getCommentCount();
    Long getPostCount();
    Long getPostPoints();
    Long getCommentPoints();
}