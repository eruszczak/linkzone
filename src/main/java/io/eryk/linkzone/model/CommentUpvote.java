package io.eryk.linkzone.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name="comment_upvote", uniqueConstraints={@UniqueConstraint(columnNames = {"account_id" , "comment_id"})})
public class CommentUpvote extends DateAudit {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @NotNull
    @Column(name = "is_upvote")
    private int isUpvote;
}
