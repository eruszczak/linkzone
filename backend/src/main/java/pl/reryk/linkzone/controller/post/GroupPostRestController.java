package pl.reryk.linkzone.controller.post;

import pl.reryk.linkzone.dto.IPostResponseDto;
import pl.reryk.linkzone.dto.PostCreate;
import pl.reryk.linkzone.dto.PostCreateLink;
import pl.reryk.linkzone.dto.PostResponse;
import pl.reryk.linkzone.exception.ValidationErrorException;
import pl.reryk.linkzone.model.Account;
import pl.reryk.linkzone.model.Group;
import pl.reryk.linkzone.model.Post;
import pl.reryk.linkzone.model.PostType;
import pl.reryk.linkzone.security.CurrentUser;
import pl.reryk.linkzone.security.UserPrincipal;
import pl.reryk.linkzone.service.FileStorageService;
import pl.reryk.linkzone.service.GroupService;
import pl.reryk.linkzone.service.PostService;
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
    private FileStorageService fileStorageService;

    @Autowired
    public GroupPostRestController(PostService postService, GroupService groupService, FileStorageService fileStorageService) {
        this.postService = postService;
        this.groupService = groupService;
        this.fileStorageService = fileStorageService;
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
