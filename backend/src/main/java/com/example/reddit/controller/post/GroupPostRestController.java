package com.example.reddit.controller.post;

import com.example.reddit.dto.*;
import com.example.reddit.exception.ValidationErrorException;
import com.example.reddit.model.Account;
import com.example.reddit.model.Group;
import com.example.reddit.model.Post;
import com.example.reddit.security.CurrentUser;
import com.example.reddit.security.UserPrincipal;
import com.example.reddit.service.FileStorageService;
import com.example.reddit.service.GroupService;
import com.example.reddit.service.PostService;
import com.example.reddit.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/groups/{groupName}/posts")
public class GroupPostRestController {

    private PostService postService;
    private GroupService groupService;
    private FileStorageService fileStorageService;

    @Autowired
    public GroupPostRestController(PostService postService, GroupService groupService, FileStorageService fileStorageService) {
        this.postService = postService;
        this.groupService = groupService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> list(@PathVariable String groupName, Pageable pageable, @CurrentUser UserPrincipal currentUser) {
        if (currentUser != null) {
            Page<IPostResponseDto> response = postService.findByGroupName(groupName, pageable, currentUser.getAccount());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Page<Post> posts = postService.findByGroupName(groupName, pageable);
        Page<PostResponse> response = posts.map(PostResponse::new);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> createTextPost(
            @PathVariable String groupName,
            @Valid @RequestBody PostCreate dto,
            Errors errors,
            @CurrentUser UserPrincipal currentUser) {
        return createPost(groupName, currentUser.getAccount(), errors, dto.getTitle(), dto.getContent(), PostType.POST);
    }

    @PostMapping(value = "/link/")
    public ResponseEntity<?> createLink(
            @PathVariable String groupName,
            @Valid @RequestBody PostCreateLink dto,
            Errors errors,
            @CurrentUser UserPrincipal currentUser) {
        return createPost(groupName, currentUser.getAccount(), errors, dto.getTitle(), dto.getContent(), PostType.LINK);
    }

    @PostMapping(value = "/media/")
    public ResponseEntity<?> createPostMedia(
            @PathVariable String groupName,
            @Valid @RequestBody PostCreate dto,
            Errors errors,
            @CurrentUser UserPrincipal currentUser) {
        return createPost(groupName, currentUser.getAccount(), errors, dto.getTitle(), dto.getContent(), PostType.MEDIA);
    }

    private ResponseEntity<?> createPost(String groupName, Account account, Errors errors, String title, String content, PostType postType) {
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        PostCreate dto = new PostCreate();
        dto.setTitle(title);
        dto.setContent(content);
        Group group = groupService.findByName(groupName);
        Post post = postService.create(dto, group, account, postType);
        return new ResponseEntity<>(new PostResponse(post), HttpStatus.CREATED);
    }
}
