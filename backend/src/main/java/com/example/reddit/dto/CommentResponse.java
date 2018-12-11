package com.example.reddit.dto;

import com.example.reddit.model.Account;
import com.example.reddit.model.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CommentResponse {

    private Long id;
    private String content;
    private List<CommentResponse> replies;
    private Instant createdAt;
    private CommentCreator author;

    public CommentResponse(Comment comment) {
        id = comment.getId();
        content = comment.getContent();
        author = new CommentCreator(comment.getAccount());
        createdAt = comment.getCreatedAt();
        replies = comment.getReplies().stream().map(CommentResponse::new).collect(Collectors.toList());
    }

    @Getter
    @Setter
    private class CommentCreator {
        private String username;
        private String avatar;

        private CommentCreator(Account account) {
            username = account.getUsername();
            avatar = account.getAvatar();
        }
    }
}
