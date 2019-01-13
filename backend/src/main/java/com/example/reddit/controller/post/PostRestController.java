package com.example.reddit.controller.post;

import com.example.reddit.dto.ICounterDto;
import com.example.reddit.dto.IPostResponseDto;
import com.example.reddit.dto.PostResponse;
import com.example.reddit.dto.PostUpdate;
import com.example.reddit.exception.ValidationErrorException;
import com.example.reddit.model.Account;
import com.example.reddit.model.Post;
import com.example.reddit.model.PostType;
import com.example.reddit.security.CurrentUser;
import com.example.reddit.security.UserPrincipal;
import com.example.reddit.service.FileStorageService;
import com.example.reddit.service.GroupService;
import com.example.reddit.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/posts")
public class PostRestController {

    private final PostService postService;

    private final FileStorageService fileStorageService;

    private final GroupService groupService;

    @Autowired
    public PostRestController(PostService postService, FileStorageService fileStorageService, GroupService groupService) {
        this.postService = postService;
        this.fileStorageService = fileStorageService;
        this.groupService = groupService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<Page<Post>> list(Pageable pageable) {
        Page<Post> posts = postService.findAll(pageable);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/top/")
    public ResponseEntity<?> top(@CurrentUser UserPrincipal currentUser, Pageable pageable) {
        Long id = null;
        if (currentUser != null) {
            id = currentUser.getId();
        }
        Page<IPostResponseDto> posts = postService.findTop(id, pageable);
        Page<PostResponse> response = posts.map(PostResponse::new);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
        Long userId = null;
        if (currentUser != null) {
            userId = currentUser.getId();
        }
        IPostResponseDto post = postService.findById(id, userId);
        Account account = currentUser == null ? null : currentUser.getAccount();
        return new ResponseEntity<>(new PostResponse(post, account), HttpStatus.OK);
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
