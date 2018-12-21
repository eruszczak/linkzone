package com.example.reddit.resource;

import com.example.reddit.controller.account.AccountRestController;
import com.example.reddit.controller.comment.AccountCommentRestController;
import com.example.reddit.controller.group.AccountGroupRestController;
import com.example.reddit.controller.post.AccountPostRestController;
import com.example.reddit.dto.AccountProfile;
import com.example.reddit.dto.AccountSummary;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class AccountResource extends ResourceSupport {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AccountProfile accountProfile;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AccountSummary accountSummary;

    public AccountResource(AccountProfile accountProfile) {
        this.accountProfile = accountProfile;
        addLinks(accountProfile.getUsername());
    }

    public AccountResource(AccountSummary accountSummary) {
        this.accountSummary = accountSummary;
        addLinks(accountSummary.getUsername());
    }

    private void addLinks(String username) {
        Pageable pageable = PageRequest.of(0, 25);
        add(linkTo(methodOn(AccountRestController.class).detail(
                username)).withSelfRel());
        add(linkTo(methodOn(AccountCommentRestController.class).list(
                username,
                pageable)).withRel("comments"));
        add(linkTo(methodOn(AccountGroupRestController.class).listGroupsSubscribedByUser(
                username)).withRel("groups"));
//        add(linkTo(methodOn(AccountPostRestController.class).list(
//                username, pageable)).withRel("posts"));
    }

    public AccountProfile getAccountProfile() {
        return accountProfile;
    }

    public AccountSummary getAccountSummary() {
        return accountSummary;
    }
}
