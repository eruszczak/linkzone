package pl.reryk.linkzone.controller.post;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import pl.reryk.linkzone.dto.ICounterDto;
import pl.reryk.linkzone.dto.IPostResponseDto;
import pl.reryk.linkzone.dto.PostResponse;
import pl.reryk.linkzone.dto.PostUpdate;
import pl.reryk.linkzone.exception.ValidationErrorException;
import pl.reryk.linkzone.model.Post;
import pl.reryk.linkzone.model.PostType;
import pl.reryk.linkzone.security.CurrentUser;
import pl.reryk.linkzone.security.UserContext;
import pl.reryk.linkzone.security.UserPrincipal;
import pl.reryk.linkzone.service.AccountService;
import pl.reryk.linkzone.service.FileStorageService;
import pl.reryk.linkzone.service.GroupService;
import pl.reryk.linkzone.service.PostService;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/posts")
@Slf4j
public class PostRestController {

    private final PostService postService;

    private final FileStorageService fileStorageService;

    private final GroupService groupService;

    private final UserContext userContext;

    private final AccountService accountService;

    @Autowired
    public PostRestController(PostService postService,
                              FileStorageService fileStorageService,
                              GroupService groupService,
                              UserContext userContext,
                              AccountService accountService) {
        this.postService = postService;
        this.fileStorageService = fileStorageService;
        this.groupService = groupService;
        this.userContext = userContext;
        this.accountService = accountService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<Page<Post>> list(Pageable pageable) {
        Page<Post> posts = postService.findAll(pageable);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/top/")
    public ResponseEntity<?> top(Pageable pageable) {
        Page<IPostResponseDto> posts = postService.findTop(userContext.context(), pageable);
        Page<PostResponse> response = posts.map(PostResponse::new);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        IPostResponseDto post = postService.findById(id, userContext.context());
        return new ResponseEntity<>(new PostResponse(post, accountService.findById(userContext.context())), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody PostUpdate updated,
                                    Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        Post post = postService.findById(id);
        if (post.getPostType().equals(PostType.MEDIA) && !Optional.ofNullable(post.getContent()).orElse("").equals(updated.getContent())) {
            fileStorageService.removeFile(post.getContent());
        }
        postService.update(post, updated);
        return new ResponseEntity<>(new PostResponse(post), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Post> delete(@PathVariable Long id) {
        Post post = postService.findById(id);
        postService.delete(post);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/clear-vote/")
    public ResponseEntity<?> clearVote(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        postService.clearVote(currentUser.getAccount(), postService.findById(id));
        return new ResponseEntity<>(postService.countUpvotes(id), HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/upvote/")
    public ResponseEntity<ICounterDto> upvote(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        postService.upvote(currentUser.getAccount(), postService.findById(id));
        return new ResponseEntity<>(postService.countUpvotes(id), HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/downvote/")
    public ResponseEntity<?> downvote(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        postService.downvote(currentUser.getAccount(), postService.findById(id));
        return new ResponseEntity<>(postService.countUpvotes(id), HttpStatus.OK);
    }
}
