package com.example.reddit.permissions.checker;

import com.example.reddit.model.Account;
import com.example.reddit.permissions.RoleName;
import org.springframework.security.core.GrantedAuthority;

public abstract class PermissionChecker {

    Account authenticatedAccount;

    PermissionChecker(Account authenticatedAccount) {
        this.authenticatedAccount = authenticatedAccount;
    }

    public boolean canRead() {
        throw new RuntimeException();
    }

    abstract public boolean canUpdate();

    abstract public boolean canDelete();

    abstract public boolean canManage();

    boolean isGlobalModOrAdmin() {
        return authenticatedAccount.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(RoleName.MODERATOR.name()) || role.equals(RoleName.ADMIN.name()));
    }
}
