package com.example.reddit.controller.comment;

import com.example.reddit.dto.CommentCreate;
import com.example.reddit.dto.CommentResponse;
import com.example.reddit.exception.ValidationErrorException;
import com.example.reddit.model.Comment;
import com.example.reddit.security.CurrentUser;
import com.example.reddit.security.UserPrincipal;
import com.example.reddit.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/api/comments")
public class CommentRestController {

    private CommentService commentService;

    @Autowired
    CommentRestController(CommentService commentService) {
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
        commentService.update(comment, updated);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Comment> delete(@PathVariable Long id) {
        // only if owner still exists  getAccount() can be null
        Comment comment = commentService.findById(id);
        commentService.delete(comment);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
