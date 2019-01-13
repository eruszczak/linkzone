package com.example.reddit.dto;

public interface IAccountStatsDto {
    Long getCommentCount();
    Long getPostCount();
    Long getPostPoints();
    Long getCommentPoints();
}