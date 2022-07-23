package io.eryk.linkzone.dto;

import io.eryk.linkzone.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostUpvoteDb {
    public Long id;
    public boolean isUpvoted;

    private Post post;
    private boolean upvoted;

    public PostUpvoteDb() {
    }

    public PostUpvoteDb(List<Object> params) {
        System.out.println(params);
    }

    public PostUpvoteDb(Long id, boolean isUpvoted) {
        this.id = id;
        this.isUpvoted = isUpvoted;
    }

    public PostUpvoteDb(Post post) {
        this.post = post;
        this.upvoted = false;
    }
}
