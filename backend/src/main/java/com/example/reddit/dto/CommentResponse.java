package com.example.reddit.dto;

import com.example.reddit.model.Account;
import com.example.reddit.model.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
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
    private String groupName;
    private Integer isUpvoted;
    private Integer upvotedCount;

    public CommentResponse(Comment comment) {
        id = comment.getId();
        content = comment.getContent();
        author = new CommentCreator(comment.getAccount());
        createdAt = comment.getCreatedAt();
        replies = comment.getReplies().stream().map(CommentResponse::new).collect(Collectors.toList());
        groupName = comment.getPost().getGroup().getName();
    }

    public CommentResponse(ICommentResponseDto dto) {
        id = dto.getId();
        content = dto.getContent();
        author = new CommentCreator(dto.getUsername(), dto.getAvatar());
        createdAt = dto.getCreatedAt();
        replies = new ArrayList<>();
        groupName = dto.getGroupName();
        isUpvoted = dto.getUpvoted();
        upvotedCount = dto.getUpvotedCount();
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

        private CommentCreator(String username, String avatar) {
            this.username = username;
            this.avatar = avatar;
        }
    }
}
