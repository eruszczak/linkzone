package com.example.reddit.permissions.checker;

import com.example.reddit.model.Account;
import com.example.reddit.permissions.RoleName;
import org.springframework.security.core.GrantedAuthority;

import java.util.Optional;

public class AccountPermissionChecker extends PermissionChecker {

    private Account account;

    public AccountPermissionChecker(Account authenticatedAccount, Account account) {
        super(authenticatedAccount);
        this.account = account;
    }

    @Override
    public boolean canUpdate() {
        return isGlobalModOrAdmin() || account.equals(authenticatedAccount);
    }

    @Override
    public boolean canDelete() {
        return canUpdate();
    }

    @Override
    public boolean canManage() {
        return false;
    }
}
