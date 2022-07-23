package io.eryk.linkzone.controller.post;

import io.eryk.linkzone.dto.IPostResponseDto;
import io.eryk.linkzone.dto.PostResponse;
import io.eryk.linkzone.model.Account;
import io.eryk.linkzone.security.CurrentUser;
import io.eryk.linkzone.security.UserPrincipal;
import io.eryk.linkzone.service.AccountService;
import io.eryk.linkzone.service.PostService;
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
@RequestMapping(value = "/api/users/{username}/posts")
public class AccountPostRestController {

    private final PostService postService;

    private final AccountService accountService;

    @Autowired
    AccountPostRestController(PostService postService, AccountService accountService) {
        this.postService = postService;
        this.accountService = accountService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> list(@PathVariable String username, @CurrentUser UserPrincipal currentUser, Pageable pageable) {

        Long id = null;
        if (currentUser != null) {
            id = currentUser.getId();
        }
        Page<IPostResponseDto> posts = postService.findByAccountUsername(username, id, pageable);
        Page<PostResponse> response = posts.map(PostResponse::new);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/upvoted/")
    public ResponseEntity<?> listUpvoted(@PathVariable String username, @CurrentUser UserPrincipal currentUser, Pageable pageable) {
        Long id = null;
        if (currentUser != null) {
            id = currentUser.getId();
        }
        Account account = accountService.findByUsername(username);
        Page<IPostResponseDto> posts = postService.findUpvoted(account.getId(), id, pageable);
        Page<PostResponse> response = posts.map(PostResponse::new);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
