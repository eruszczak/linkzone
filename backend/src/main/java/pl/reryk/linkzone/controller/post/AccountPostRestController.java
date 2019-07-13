package pl.reryk.linkzone.controller.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.reryk.linkzone.dto.IPostResponseDto;
import pl.reryk.linkzone.dto.PostResponse;
import pl.reryk.linkzone.model.Account;
import pl.reryk.linkzone.security.CurrentUser;
import pl.reryk.linkzone.security.UserContext;
import pl.reryk.linkzone.security.UserPrincipal;
import pl.reryk.linkzone.service.AccountService;
import pl.reryk.linkzone.service.PostService;

@RestController
@RequestMapping(value = "/api/users/{username}/posts")
public class AccountPostRestController {

    private final PostService postService;

    private final AccountService accountService;

    private final UserContext userContext;

    @Autowired
    AccountPostRestController(PostService postService, AccountService accountService, UserContext userContext) {
        this.postService = postService;
        this.accountService = accountService;
        this.userContext = userContext;
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
