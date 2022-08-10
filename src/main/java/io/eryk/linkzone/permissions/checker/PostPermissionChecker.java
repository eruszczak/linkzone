package io.eryk.linkzone.permissions.checker;

import io.eryk.linkzone.model.Account;
import io.eryk.linkzone.model.Post;


public class PostPermissionChecker extends PermissionChecker {

    private final Post post;
    private final GroupPermissionChecker groupPermissionChecker;

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
