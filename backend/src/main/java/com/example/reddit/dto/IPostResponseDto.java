package com.example.reddit.dto;

import com.example.reddit.controller.post.PostType;

public interface IPostResponseDto {
    Long getId();
    String getTitle();
    String getContent();
    String getSlug();
    PostType getType();
    String getAuthor();
    String getGroupName();
    boolean getLocked();
    Integer getUpvoted();
    Integer getUpvotedUser();
    Integer getUpvotedCount();

}