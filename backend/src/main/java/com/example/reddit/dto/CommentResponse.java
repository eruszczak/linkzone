package com.example.reddit.dto;

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
    private String author;
    private Instant createdAt;

    public CommentResponse(Comment comment) {
        id = comment.getId();
        content = comment.getContent();
        author = comment.getAccount().getUsername();
        createdAt = comment.getCreatedAt();
        replies = comment.getReplies().stream().map(CommentResponse::new).collect(Collectors.toList());
    }
}
