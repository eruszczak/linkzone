package com.example.reddit.controller.post;

import com.example.reddit.dto.IPostResponseDto;
import com.example.reddit.dto.PostResponse;
import com.example.reddit.model.Post;
import com.example.reddit.security.CurrentUser;
import com.example.reddit.security.UserPrincipal;
import com.example.reddit.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/users/{username}/posts")
public class AccountPostRestController {

    private PostService postService;

    @Autowired
    AccountPostRestController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> list(@PathVariable String username, Pageable pageable) {
        Page<Post> posts = postService.findByAccountUsername(username, pageable);
        Page<PostResponse> response = posts.map(PostResponse::new);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/upvoted/")
    public ResponseEntity<?> listUpvoted(@PathVariable String username, @CurrentUser UserPrincipal currentUser, Pageable pageable) {
        Page<IPostResponseDto> posts = postService.findUpvoted(currentUser.getAccount(), pageable);
//        Page<PostResponse> response = posts.map(PostResponse::new);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
}
