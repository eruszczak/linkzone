package io.eryk.linkzone.controller.comment;

import io.eryk.linkzone.dto.CommentCreate;
import io.eryk.linkzone.dto.CommentResponse;
import io.eryk.linkzone.exception.ValidationErrorException;
import io.eryk.linkzone.model.Comment;
import io.eryk.linkzone.security.CurrentUser;
import io.eryk.linkzone.security.UserPrincipal;
import io.eryk.linkzone.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/api/comments")
public class CommentRestController {

    private final CommentService commentService;

    @Autowired
    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<Page<Comment>> list(Pageable pageable) {
        Page<Comment> comments = commentService.findAll(pageable);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Comment> detail(@PathVariable Long id) {
        Comment comment = commentService.findById(id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<?> createReply(@PathVariable Long id,
                                         @Valid @RequestBody CommentCreate commentReply,
                                         Errors errors,
                                         @CurrentUser UserPrincipal currentUser) {
        Comment comment = commentService.findById(id);
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        Comment createdComment = commentService.createReply(comment, commentReply, currentUser.getAccount());
        return new ResponseEntity<>(new CommentResponse(createdComment), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody CommentCreate updated,
                                    Errors errors,
                                    @CurrentUser UserPrincipal currentUser) {
        // only if owner still exists. getAccount() can be null
        Comment comment = commentService.findById(id);
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        comment = commentService.update(comment, updated);
        return new ResponseEntity<>(new CommentResponse(comment), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Comment> delete(@PathVariable Long id) {
        // only if owner still exists  getAccount() can be null
        Comment comment = commentService.findById(id);
        commentService.delete(comment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/clear-vote/")
    public ResponseEntity<?> clearVote(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        commentService.clearVote(currentUser.getAccount(), commentService.findById(id));
        return new ResponseEntity<>(commentService.countUpvotes(id), HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/upvote/")
    public ResponseEntity<?> upvote(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        commentService.upvote(currentUser.getAccount(), commentService.findById(id));
        return new ResponseEntity<>(commentService.countUpvotes(id), HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/downvote/")
    public ResponseEntity<?> downvote(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        commentService.downvote(currentUser.getAccount(), commentService.findById(id));
        return new ResponseEntity<>(commentService.countUpvotes(id), HttpStatus.OK);
    }
}
