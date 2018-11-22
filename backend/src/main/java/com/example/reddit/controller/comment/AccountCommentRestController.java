package com.example.reddit.controller.comment;

import com.example.reddit.controller.account.AccountRestController;
import com.example.reddit.model.Comment;
import com.example.reddit.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api" + "/users" + "/{username}" + "/comments")
public class AccountCommentRestController {

    private CommentService commentService;

    @Autowired
    AccountCommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<Page<Comment>> list(@PathVariable String username, Pageable pageable) {
        Page<Comment> comments = commentService.findByAccountUsername(username, pageable);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
