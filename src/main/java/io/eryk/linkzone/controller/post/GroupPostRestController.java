package io.eryk.linkzone.controller.post;

import io.eryk.linkzone.dto.IPostResponseDto;
import io.eryk.linkzone.dto.PostCreate;
import io.eryk.linkzone.dto.PostCreateLink;
import io.eryk.linkzone.dto.PostResponse;
import io.eryk.linkzone.exception.ValidationErrorException;
import io.eryk.linkzone.model.Account;
import io.eryk.linkzone.model.Group;
import io.eryk.linkzone.model.Post;
import io.eryk.linkzone.model.PostType;
import io.eryk.linkzone.security.CurrentUser;
import io.eryk.linkzone.security.UserPrincipal;
import io.eryk.linkzone.service.GroupService;
import io.eryk.linkzone.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/groups/{groupName}/posts")
public class GroupPostRestController {

    private final PostService postService;
    private final GroupService groupService;

    @Autowired
    public GroupPostRestController(PostService postService, GroupService groupService) {
        this.postService = postService;
        this.groupService = groupService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> list(@PathVariable String groupName, Pageable pageable, @CurrentUser UserPrincipal currentUser) {
        Long userId = currentUser != null ? currentUser.getAccount().getId() : - 1;
        Page<IPostResponseDto> posts = postService.findByGroupName(groupName, pageable, userId);
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
