package com.example.reddit.dto;

import com.example.reddit.controller.post.PostType;
import com.example.reddit.model.Account;
import com.example.reddit.model.Group;
import com.example.reddit.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

public class PostResponse {
    public long id;
    public String title;
    public String content;
    public String slug;
    public PostType type;
    public String author;
    public String groupName;
    public boolean locked;
    public Integer isUpvoted;
    public Integer upvotedCount;
    public int commentCount;
    public Instant createdAt;

    public boolean isCreator;

    public PostResponse(Post post) {
        id = post.getId();
        title = post.getTitle();
        content = post.getContent();
        slug = post.getSlug();
        type = post.getPostType();
        author = post.getAccount().getUsername();
        groupName = post.getGroup().getName();
        locked = post.locked();
    }

    public PostResponse(IPostResponseDto dto) {
        id = dto.getId();
        title = dto.getTitle();
        content = dto.getContent();
        slug = dto.getSlug();
        type = dto.getType();
        author = dto.getAuthor();
        groupName = dto.getGroupName();
        locked = dto.getLocked();
        isUpvoted = dto.getUpvoted();
        upvotedCount = dto.getUpvotedCount();
        commentCount = dto.getCommentCount();
        createdAt = dto.getCreatedAt();
    }

    public PostResponse(IPostResponseDto dto, Account requestUser) {
        this(dto);
        if (requestUser != null) {
            isCreator = dto.getAuthor().equals(requestUser.getUsername());
        }
    }
}