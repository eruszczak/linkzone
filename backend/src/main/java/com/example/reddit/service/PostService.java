package com.example.reddit.service;

import com.example.reddit.controller.post.PostType;
import com.example.reddit.dto.PostCreate;
import com.example.reddit.dto.PostUpdate;
import com.example.reddit.exception.NotFoundException;
import com.example.reddit.model.Account;
import com.example.reddit.model.Group;
import com.example.reddit.model.Post;
import com.example.reddit.permissions.Permissions;
import com.example.reddit.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findByGroupName(String name) {
        return postRepository.findByGroupName(name);
    }

    public Page<Post> findByGroupName(String groupName, Pageable pageable) {
        return postRepository.findByGroupName(groupName, pageable);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post create(PostCreate dto, Group group, Account account, PostType postType) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setPostType(postType);
        post.setAccount(account);
        post.setGroup(group);
        return save(post);
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new NotFoundException(Post.class, id.toString()));
    }

    public boolean existsById(Long aLong) {
        return postRepository.existsById(aLong);
    }

    public long count() {
        return postRepository.count();
    }

    @PreAuthorize("hasPermission(#post, '" + Permissions.DELETE + "')")
    public void delete(Post post) {
        postRepository.delete(post);
    }

    public void deleteNoPerm(Post post) {
        postRepository.delete(post);
    }

    public void deleteAll() {
        postRepository.deleteAll();
    }

    @PreAuthorize("hasPermission(#post, '" + Permissions.UPDATE + "')")
    public Post update(Post post, PostUpdate updated) {
        return updatePost(post, updated);
    }

    public Post updateNoPerms(Post post, PostUpdate updated) {
        return updatePost(post, updated);
    }

    private Post updatePost(Post post, PostUpdate updated) {
        post.setLocked(updated.isLocked());
        post.setContent(updated.getContent());
        post.setTitle(updated.getTitle());
        try {
            managePost(post, updated);
        } catch (Exception e) {
            System.out.println(e);
        }
        return save(post);
    }

    @PreAuthorize("hasPermission(#post, '" + Permissions.MANAGE + "')")
    private void managePost(Post post, PostUpdate update) {
        post.setLocked(update.locked());
    }

    public Page<Post> findByAccountUsername(String username, Pageable pageable) {
        return postRepository.findByAccountUsername(username, pageable);
    }
}
