package com.example.reddit.permissions.checker;

import com.example.reddit.model.Account;
import com.example.reddit.model.Group;


public class GroupPermissionChecker extends PermissionChecker {

    private Group group;

    public GroupPermissionChecker(Account authenticatedAccount, Group group) {
        super(authenticatedAccount);
        this.group = group;
    }

    @Override
    public boolean canUpdate() {
        return group.getCreator().equals(authenticatedAccount) ||
                group.getAdministrators().contains(authenticatedAccount);
    }

    @Override
    public boolean canDelete() {
        return canUpdate();
    }

    /**
     * @return true if account has rights to the group - is its creator/admin/mod or global admin/mod
     */
    public boolean canModerate() {
        return isGlobalModOrAdmin() ||
                group.getCreator().equals(authenticatedAccount) ||
                group.getAdministrators().contains(authenticatedAccount) ||
                group.getModerators().contains(authenticatedAccount);
    }

    @Override
    public boolean canManage() {
        return false;
    }
}
