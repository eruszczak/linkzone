package pl.reryk.linkzone.controller.comment;

import pl.reryk.linkzone.dto.CommentCreate;
import pl.reryk.linkzone.dto.CommentResponse;
import pl.reryk.linkzone.dto.ICommentResponseDto;
import pl.reryk.linkzone.exception.ResourceLockedException;
import pl.reryk.linkzone.exception.ValidationErrorException;
import pl.reryk.linkzone.model.Comment;
import pl.reryk.linkzone.model.Post;
import pl.reryk.linkzone.security.CurrentUser;
import pl.reryk.linkzone.security.UserPrincipal;
import pl.reryk.linkzone.service.CommentService;
import pl.reryk.linkzone.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/posts/{postId}/comments")
public class PostCommentRestController {

    private CommentService commentService;
    private PostService postService;

    @Autowired
    PostCommentRestController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> list(@PathVariable Long postId, Pageable pageable, @CurrentUser UserPrincipal currentUser) {
        Long userId = currentUser != null ? currentUser.getAccount().getId() : null;
        Page<ICommentResponseDto> comments = commentService.findByPostId(postId, userId, pageable);
        List<CommentResponse> result = new ArrayList<>();
        String requestUsername = currentUser != null ? currentUser.getUsername() : "";
        CommentResponse previousParent = null;
        for (ICommentResponseDto dto : comments.getContent()) {
            CommentResponse comment = new CommentResponse(dto);
            comment.isCreator = requestUsername.equals(dto.getUsername());
            if (dto.getParentId() == null) {
                result.add(comment);
                previousParent = comment;
            } else if (previousParent != null) {
                previousParent.addReply(comment);
            }
        }
        Page<CommentResponse> responses = new PageImpl<>(result, pageable, result.size());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> create(@PathVariable Long postId,
                                    @Valid @RequestBody CommentCreate dto,
                                    Errors errors,
                                    @CurrentUser UserPrincipal currentUser) {
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        Post post = postService.findById(postId);
        if (!post.locked()) {
            Comment comment = commentService.create(dto, post, currentUser.getAccount());
            return new ResponseEntity<>(new CommentResponse(comment), HttpStatus.CREATED);
        }
        throw new ResourceLockedException("Post is locked");
    }
}
