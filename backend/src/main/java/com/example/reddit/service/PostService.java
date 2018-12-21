package com.example.reddit.service;

import com.example.reddit.controller.post.PostType;
import com.example.reddit.dto.IPostResponseDto;
import com.example.reddit.dto.PostCreate;
import com.example.reddit.dto.PostUpdate;
import com.example.reddit.exception.NotFoundException;
import com.example.reddit.model.Account;
import com.example.reddit.model.Group;
import com.example.reddit.model.Post;
import com.example.reddit.model.PostUpvote;
import com.example.reddit.permissions.Permissions;
import com.example.reddit.repository.PostRepository;
import com.example.reddit.repository.PostUpvoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final PostUpvoteRepository postUpvoteRepository;

    @Autowired
    public PostService(PostRepository postRepository, PostUpvoteRepository postUpvoteRepository) {
        this.postRepository = postRepository;
        this.postUpvoteRepository = postUpvoteRepository;
    }

    public List<Post> findByGroupName(String name) {
        return postRepository.findByGroupName(name);
    }

    public Page<IPostResponseDto> findByGroupName(String groupName, Pageable pageable, Long accountId) {
        return postRepository.findByGroupName(groupName, pageable, accountId);
    }

    public Page<IPostResponseDto> findUpvoted(Account account, Pageable pageable) {
        return postRepository.findUpvoted(account.getId(), pageable);
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

    public IPostResponseDto findById(Long idPost, Long accountId) {
        return postRepository.find(idPost, accountId).orElseThrow(() -> new NotFoundException(Post.class, idPost.toString()));
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

    // todo check permission if owner
    public Page<IPostResponseDto> findTop(Long accountId, Pageable pageable) {
        if (accountId == null) {
            return postRepository.findTop(pageable);
        }
        return postRepository.findTop(accountId, pageable);
    }

//    public Page<Post> findTop(Pageable pageable) {
//
//    }

    public void clearVote(Account account, Post post) {
        // TODO set to null or remove?
        postUpvoteRepository.deleteByAccountIdAndPostId(account.getId(), post.getId());
    }

    public void upvote(Account account, Post post) {
        PostUpvote postUpvote = getOrCreatePostUpvote(account, post);
        postUpvote.setIsUpvote(1);
        postUpvoteRepository.save(postUpvote);
    }

    public void downvote(Account account, Post post) {
        PostUpvote postUpvote = getOrCreatePostUpvote(account, post);
        postUpvote.setIsUpvote(-1);
        postUpvoteRepository.save(postUpvote);
    }

    private PostUpvote getOrCreatePostUpvote(Account account, Post post) {
        Optional<PostUpvote> obj = postUpvoteRepository.findByAccountIdAndPostId(account.getId(), post.getId());
        PostUpvote postUpvote;
        if (obj.isPresent()) {
            postUpvote = obj.get();
        } else {
            postUpvote = new PostUpvote();
            postUpvote.setAccount(account);
            postUpvote.setPost(post);
        }
        return postUpvote;
    }
}
