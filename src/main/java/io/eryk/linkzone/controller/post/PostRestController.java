package io.eryk.linkzone.controller.post;

import io.eryk.linkzone.dto.ICounterDto;
import io.eryk.linkzone.dto.IPostResponseDto;
import io.eryk.linkzone.dto.PostResponse;
import io.eryk.linkzone.dto.PostUpdate;
import io.eryk.linkzone.exception.ValidationErrorException;
import io.eryk.linkzone.model.Post;
import io.eryk.linkzone.model.PostType;
import io.eryk.linkzone.security.CurrentUser;
import io.eryk.linkzone.security.UserContext;
import io.eryk.linkzone.security.UserPrincipal;
import io.eryk.linkzone.service.AccountService;
import io.eryk.linkzone.service.FileStorageService;
import io.eryk.linkzone.service.PostService;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/posts")
@Slf4j
public class PostRestController {

    private final PostService postService;

    private final FileStorageService fileStorageService;

    private final UserContext userContext;

    private final AccountService accountService;

    @Autowired
    public PostRestController(PostService postService,
                              FileStorageService fileStorageService,
                              UserContext userContext,
                              AccountService accountService) {
        this.postService = postService;
        this.fileStorageService = fileStorageService;
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
