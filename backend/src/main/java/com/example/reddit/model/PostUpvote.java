package com.example.reddit.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name="post_upvote", uniqueConstraints={@UniqueConstraint(columnNames = {"account_id" , "post_id"})})
public class PostUpvote extends DateAudit {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotNull
    @Column(name = "is_upvote")
    private boolean isUpvote;
}
