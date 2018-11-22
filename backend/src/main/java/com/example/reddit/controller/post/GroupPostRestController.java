package com.example.reddit.controller.post;

import com.example.reddit.controller.group.GroupRestController;
import com.example.reddit.dto.PostCreate;
import com.example.reddit.dto.PostCreateLink;
import com.example.reddit.dto.PostResponse;
import com.example.reddit.exception.ValidationErrorException;
import com.example.reddit.model.Group;
import com.example.reddit.model.Post;
import com.example.reddit.security.CurrentUser;
import com.example.reddit.security.UserPrincipal;
import com.example.reddit.service.GroupService;
import com.example.reddit.service.PostService;
import com.example.reddit.validation.ValidationErrorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/groups/{groupName}/posts")
public class GroupPostRestController {

    private PostService postService;
    private GroupService groupService;

    @Autowired
    GroupPostRestController(PostService postService, GroupService groupService) {
        this.postService = postService;
        this.groupService = groupService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> list(@PathVariable String groupName, Pageable pageable) {
        Page<Post> posts = postService.findByGroupName(groupName, pageable);
        Page<PostResponse> response = posts.map(PostResponse::new);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> createPost(
            @PathVariable String groupName,
            @Valid @RequestBody PostCreate dto,
            Errors errors,
            @CurrentUser UserPrincipal currentUser) {
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        Group group = groupService.findByName(groupName);
        Post post = postService.create(dto, group, currentUser.getAccount());
        return new ResponseEntity<>(new PostResponse(post), HttpStatus.CREATED);
    }

    @PostMapping(value = "/link/")
    public ResponseEntity<?> createLink(
            @PathVariable String groupName,
            @Valid @RequestBody PostCreateLink dto,
            Errors errors,
            @CurrentUser UserPrincipal currentUser) {
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        Group group = groupService.findByName(groupName);
        Post post = postService.create(dto, group, currentUser.getAccount());
        return new ResponseEntity<>(new PostResponse(post), HttpStatus.CREATED);
    }
}
