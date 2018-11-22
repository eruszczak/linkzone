package com.example.reddit.dto;

import com.example.reddit.controller.post.PostType;
import com.example.reddit.model.Post;

public class PostResponse {
    public long id;
    public String title;
    public String content;
    public String slug;
    public PostType type;
    public String author;
    public String groupName;
    public boolean locked;

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
}