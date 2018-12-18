package com.example.reddit.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class PostUpvote extends DateAudit {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    private Account account;

    @ManyToOne
    @NotNull
    private Post post;
}
