package com.example.reddit.service;

import com.example.reddit.dto.CommentCreate;
import com.example.reddit.exception.NotFoundException;
import com.example.reddit.exception.ResourceLockedException;
import com.example.reddit.model.Account;
import com.example.reddit.model.Comment;
import com.example.reddit.model.Post;
import com.example.reddit.permissions.Permissions;
import com.example.reddit.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public Page<Comment> findAll(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    public Page<Comment> findReplies(Long postId, Pageable pageable) {
        return commentRepository.findByParentIdAndParentParentIsNull(postId, pageable);
    }

    public Comment createReply(Comment parentComment, CommentCreate replyComment, Account account) {
        Comment comment = mapToComment(replyComment, account, parentComment.getPost());
        comment.setParent(parentComment);
        return save(comment);
    }

    public Comment create(CommentCreate commentCreate, Post post, Account account) {
        if (post.locked()) {
            throw new ResourceLockedException("post_locked");
        }
        Comment comment = mapToComment(commentCreate, account, post);
        return save(comment);
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Comment.class, id.toString()));
    }

    public boolean existsById(Long aLong) {
        return commentRepository.existsById(aLong);
    }

    public long count() {
        return commentRepository.count();
    }

    @PreAuthorize("hasPermission(#comment, '" + Permissions.DELETE + "')")
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    public void deleteAll() {
        commentRepository.deleteAll();
    }

    @PreAuthorize("hasPermission(#comment, '" + Permissions.UPDATE + "')")
    public void update(Comment comment, CommentCreate updated) {
        comment.setContent(updated.getContent());
        save(comment);
    }

    public Page<Comment> findByAccountUsername(String username, Pageable pageable) {
        return commentRepository.findByAccountUsername(username, pageable);
    }

    public List<Comment> findByPostId(Long id) {
        return commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtDesc(id);
    }

    public Page<Comment> findByPostId(Long id, Pageable pageable) {
        return commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtDesc(id, pageable);
    }

    private Comment mapToComment(CommentCreate commentCreate, Account account, Post post) {
        Comment comment = new Comment();
        comment.setAccount(account);
        comment.setPost(post);
        comment.setContent(commentCreate.getContent());
        return comment;
    }
}
