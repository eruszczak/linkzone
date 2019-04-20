package pl.reryk.linkzone.permissions.checker;

import pl.reryk.linkzone.model.Account;

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
