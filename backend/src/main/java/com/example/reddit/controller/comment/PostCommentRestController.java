package com.example.reddit.controller.comment;

import com.example.reddit.dto.CommentCreate;
import com.example.reddit.dto.CommentResponse;
import com.example.reddit.exception.ResourceLockedException;
import com.example.reddit.exception.ValidationErrorException;
import com.example.reddit.model.Comment;
import com.example.reddit.model.Post;
import com.example.reddit.security.CurrentUser;
import com.example.reddit.security.UserPrincipal;
import com.example.reddit.service.CommentService;
import com.example.reddit.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/posts/{postId}/comments")
public class PostCommentRestController {

    private CommentService commentService;
    private PostService postService;

    @Autowired
    PostCommentRestController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> list(@PathVariable Long postId, Pageable pageable) {
        Page<Comment> comments = commentService.findByPostId(postId, pageable);
        Page<CommentResponse> responses = comments.map(CommentResponse::new);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> create(@PathVariable Long postId,
                                    @Valid @RequestBody CommentCreate dto,
                                    Errors errors,
                                    @CurrentUser UserPrincipal currentUser) {
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        Post post = postService.findById(postId);
        if (!post.locked()) {
            Comment comment = commentService.create(dto, post, currentUser.getAccount());
            return new ResponseEntity<>(new CommentResponse(comment), HttpStatus.CREATED);
        }
        throw new ResourceLockedException("Post is locked");
    }
}
