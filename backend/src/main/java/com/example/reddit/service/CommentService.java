package com.example.reddit.service;

import com.example.reddit.dto.CommentCreate;
import com.example.reddit.dto.ICommentResponseDto;
import com.example.reddit.dto.ICounterDto;
import com.example.reddit.exception.NotFoundException;
import com.example.reddit.exception.ResourceLockedException;
import com.example.reddit.model.*;
import com.example.reddit.permissions.Permissions;
import com.example.reddit.repository.CommentRepository;
import com.example.reddit.repository.CommentUpvoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final CommentUpvoteRepository commentUpvoteRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, CommentUpvoteRepository commentUpvoteRepository) {
        this.commentRepository = commentRepository;
        this.commentUpvoteRepository = commentUpvoteRepository;
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

    public Page<ICommentResponseDto> findUpvoted(Long userId, Long accountId, Pageable pageable) {
        return commentRepository.findUpvoted(userId, accountId, pageable);
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
    public Comment update(Comment comment, CommentCreate updated) {
        comment.setContent(updated.getContent());
        return save(comment);
    }

    public Page<ICommentResponseDto> findByAccountUsername(String username, Long accountId, Pageable pageable) {
        return commentRepository.findByAccount(username, accountId, pageable);
    }

    public List<Comment> findByPostId(Long id) {
        return commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtDesc(id);
    }

    public Page<Comment> findByPostId(Long id, Pageable pageable) {
        return commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtDesc(id, pageable);
    }

    public Page<ICommentResponseDto> findByPostId(Long id, Long accountId, Pageable pageable) {
        return commentRepository.findByPostIdWithReplies(id, accountId, pageable);
    }

    public void clearVote(Account account, Comment comment) {
        commentUpvoteRepository.deleteByAccountIdAndCommentId(account.getId(), comment.getId());
    }

    public void upvote(Account account, Comment comment) {
        CommentUpvote commentUpvote = getOrCreateCommentUpvote(account, comment);
        commentUpvote.setIsUpvote(1);
        commentUpvoteRepository.save(commentUpvote);
    }

    public void downvote(Account account, Comment comment) {
        CommentUpvote commentUpvote = getOrCreateCommentUpvote(account, comment);
        commentUpvote.setIsUpvote(-1);
        commentUpvoteRepository.save(commentUpvote);
    }

    public ICounterDto countUpvotes(Long commentId) {
        return commentUpvoteRepository.getCounter(commentId);
    }

    private CommentUpvote getOrCreateCommentUpvote(Account account, Comment comment) {
        Optional<CommentUpvote> obj = commentUpvoteRepository.findByAccountIdAndCommentId(account.getId(), comment.getId());
        CommentUpvote commentUpvote;
        if (obj.isPresent()) {
            commentUpvote = obj.get();
        } else {
            commentUpvote = new CommentUpvote();
            commentUpvote.setAccount(account);
            commentUpvote.setComment(comment);
        }
        return commentUpvote;
    }

    private Comment mapToComment(CommentCreate commentCreate, Account account, Post post) {
        Comment comment = new Comment();
        comment.setAccount(account);
        comment.setPost(post);
        comment.setContent(commentCreate.getContent());
        return comment;
    }
}
