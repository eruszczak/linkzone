package com.example.reddit.dto;

import com.example.reddit.controller.post.PostType;

import java.time.Instant;

public interface IPostResponseDto {
    Long getId();
    String getTitle();
    String getContent();
    String getSlug();
    Instant getCreatedAt();
    PostType getType();
    int getCommentCount();
    String getAuthor();
    String getAuthorAvatar();
    String getGroupName();
    String getGroupLogo();
    boolean getLocked();
    Integer getUpvoted();
    Integer getUpvotedUser();
    Integer getUpvotedCount();

}