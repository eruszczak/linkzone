package com.example.reddit.controller.comment;

import com.example.reddit.dto.CommentResponse;
import com.example.reddit.dto.ICommentResponseDto;
import com.example.reddit.dto.IPostResponseDto;
import com.example.reddit.dto.PostResponse;
import com.example.reddit.model.Account;
import com.example.reddit.model.Comment;
import com.example.reddit.security.CurrentUser;
import com.example.reddit.security.UserPrincipal;
import com.example.reddit.service.AccountService;
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
@RequestMapping(value = "/api/users/{username}/comments")
public class AccountCommentRestController {

    private CommentService commentService;

    private AccountService accountService;

    @Autowired
    AccountCommentRestController(CommentService commentService, AccountService accountService) {
        this.commentService = commentService;
        this.accountService = accountService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> list(@PathVariable String username, @CurrentUser UserPrincipal currentUser, Pageable pageable) {
        Long id = null;
        if (currentUser != null) {
            id = currentUser.getId();
        }
        Page<ICommentResponseDto> comments = commentService.findByAccountUsername(username, id, pageable);
        Page<CommentResponse> commentResponses = comments.map(CommentResponse::new);
        return new ResponseEntity<>(commentResponses, HttpStatus.OK);
    }

    @GetMapping(value = "/upvoted/")
    public ResponseEntity<?> listUpvoted(@PathVariable String username, @CurrentUser UserPrincipal currentUser, Pageable pageable) {
        Long id = null;
        if (currentUser != null) {
            id = currentUser.getId();
        }
        Account account = accountService.findByUsername(username);
        Page<ICommentResponseDto> comments = commentService.findUpvoted(account.getId(), id, pageable);
        Page<CommentResponse> response = comments.map(CommentResponse::new);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
