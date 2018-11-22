package com.example.reddit.controller.post;

import com.example.reddit.controller.account.AccountRestController;
import com.example.reddit.dto.PostResponse;
import com.example.reddit.model.Post;
import com.example.reddit.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api" + "/users" + "/{username}" + "/posts")
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
}
