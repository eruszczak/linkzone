package com.example.reddit.dto;

import com.example.reddit.controller.post.PostType;
import com.example.reddit.model.Account;
import com.example.reddit.model.Group;
import com.example.reddit.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PostResponse {
    private long id;
    private String title;
    private String content;
    private String slug;
    private PostType type;
    private String author;
    private String groupName;
    private boolean locked;
    private Integer isUpvoted;
    private Integer upvotedCount;
    private int commentCount;
    private boolean isCreator;
    private Instant createdAt;

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

    public PostResponse(IPostResponseDto dto, Account account) {
        this(dto);
        isCreator = account != null && author.equals(account.getUsername());
    }
}