package io.eryk.linkzone.controller.comment;

import io.eryk.linkzone.dto.CommentCreate;
import io.eryk.linkzone.dto.CommentResponse;
import io.eryk.linkzone.dto.ICommentResponseDto;
import io.eryk.linkzone.exception.ResourceLockedException;
import io.eryk.linkzone.exception.ValidationErrorException;
import io.eryk.linkzone.model.Comment;
import io.eryk.linkzone.model.Post;
import io.eryk.linkzone.security.CurrentUser;
import io.eryk.linkzone.security.UserPrincipal;
import io.eryk.linkzone.service.CommentService;
import io.eryk.linkzone.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/posts/{postId}/comments")
public class PostCommentRestController {

    private final CommentService commentService;
    private final PostService postService;

    @Autowired
    PostCommentRestController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> list(@PathVariable Long postId, Pageable pageable, @CurrentUser UserPrincipal currentUser) {
        Long userId = currentUser != null ? currentUser.getAccount().getId() : null;
        Page<ICommentResponseDto> comments = commentService.findByPostId(postId, userId, pageable);
        List<CommentResponse> result = new ArrayList<>();
        String requestUsername = currentUser != null ? currentUser.getUsername() : "";
        CommentResponse previousParent = null;
        for (ICommentResponseDto dto : comments.getContent()) {
            CommentResponse comment = new CommentResponse(dto);
            comment.isCreator = requestUsername.equals(dto.getUsername());
            if (dto.getParentId() == null) {
                result.add(comment);
                previousParent = comment;
            } else if (previousParent != null) {
                previousParent.addReply(comment);
            }
        }
        Page<CommentResponse> responses = new PageImpl<>(result, pageable, result.size());
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
