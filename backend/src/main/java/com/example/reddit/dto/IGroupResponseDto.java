package com.example.reddit.dto;

import java.time.Instant;

public interface IGroupResponseDto {
    Long getId();
    String getName();
    String getDescription();
    String getBannerUrl();
    String getLogo();
    Instant getCreatedAt();
    boolean getIsDefault();

    int getSubscribers();
    int getIsSubbed();
    Integer getPostCount();
    String getGroupStatus();
    String getPostTypes();
}