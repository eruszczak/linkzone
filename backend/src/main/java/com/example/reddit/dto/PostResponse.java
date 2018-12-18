package com.example.reddit.dto;

import com.example.reddit.controller.post.PostType;
import com.example.reddit.model.Post;
import lombok.Getter;
import lombok.Setter;

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
    private boolean isUpvoted;

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

    public PostResponse(PostUpvoteDb postUpvoteDb) {
        this(postUpvoteDb.getPost());
        isUpvoted = postUpvoteDb.isUpvoted();
    }
}