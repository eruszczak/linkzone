package com.example.reddit.dto;

import com.example.reddit.controller.post.PostType;

import java.time.Instant;

public interface IGroupResponseDto {
    Long getId();
    String getName();
    String getDescription();
    String getBannerUrl();
    Instant getCreatedAt();

    int getSubscribers();
    int getIsSubbed();
}