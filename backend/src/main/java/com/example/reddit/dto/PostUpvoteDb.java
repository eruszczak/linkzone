package com.example.reddit.dto;

import com.example.reddit.model.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpvoteDb {
    private Post post;
    private boolean upvoted;

    public PostUpvoteDb(Post post, boolean upvoted) {
        this.post = post;
        this.upvoted = upvoted;
    }
}
