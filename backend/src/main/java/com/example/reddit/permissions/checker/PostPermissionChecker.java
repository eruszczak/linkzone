package com.example.reddit.permissions.checker;

import com.example.reddit.model.Account;
import com.example.reddit.model.Post;


public class PostPermissionChecker extends PermissionChecker {

    private Post post;
    private GroupPermissionChecker groupPermissionChecker;

    public PostPermissionChecker(Account authenticatedAccount, Post post) {
        super(authenticatedAccount);
        this.post = post;
        groupPermissionChecker = new GroupPermissionChecker(authenticatedAccount, post.getGroup());
    }

    @Override
    public boolean canUpdate() {
        return post.getAccount().equals(authenticatedAccount) || groupPermissionChecker.canModerate();
    }

    @Override
    public boolean canDelete() {
        return canUpdate();
    }

    @Override
    public boolean canManage() {
        return groupPermissionChecker.canModerate();
    }
}
