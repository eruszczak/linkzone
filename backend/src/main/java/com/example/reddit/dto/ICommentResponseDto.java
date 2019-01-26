package com.example.reddit.dto;

import java.time.Instant;

public interface ICommentResponseDto {
    Long getId();
    String getContent();
    Long getPostId();
    Long getParentId();
    Instant getCreatedAt();
    String getPostTitle();
    String getGroupName();
    String getUsername();
    String getAvatar();
    Integer getUpvoted();
    Integer getUpvotedUser();
    Integer getUpvotedCount();
    String getPostSlug();
}
