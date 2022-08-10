package io.eryk.linkzone.permissions;

import io.eryk.linkzone.model.Account;
import io.eryk.linkzone.model.Comment;
import io.eryk.linkzone.model.Group;
import io.eryk.linkzone.model.Post;
import io.eryk.linkzone.permissions.checker.AccountPermissionChecker;
import io.eryk.linkzone.permissions.checker.CommentPermissionChecker;
import io.eryk.linkzone.permissions.checker.GroupPermissionChecker;
import io.eryk.linkzone.permissions.checker.PermissionChecker;
import io.eryk.linkzone.permissions.checker.PostPermissionChecker;
import io.eryk.linkzone.security.UserPrincipal;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;


public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if (auth == null || !auth.isAuthenticated() || targetDomainObject == null) {
            return false;
        }
        Account account = ((UserPrincipal) auth.getPrincipal()).getAccount();
        PermissionChecker permissionChecker = getPermissionChecker(account, targetDomainObject);
        switch (permission.toString()) {
            case Permissions.UPDATE:
                return permissionChecker.canUpdate();
            case Permissions.DELETE:
                return permissionChecker.canDelete();
            case Permissions.READ:
                return permissionChecker.canRead();
            case Permissions.MANAGE:
                return permissionChecker.canManage();
        }
        throw new RuntimeException();
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        return false;
    }

//    private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
//        for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
//            if (grantedAuth.getAuthority().startsWith(targetType)) {
//                if (grantedAuth.getAuthority().contains(permission)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    private PermissionChecker getPermissionChecker(Account account, Object object) {
        if (object instanceof Group) {
            return new GroupPermissionChecker(account, (Group) object);
        } else if (object instanceof Post) {
            return new PostPermissionChecker(account, (Post) object);
        } else if (object instanceof Comment) {
            return new CommentPermissionChecker(account, (Comment) object);
        } else if (object instanceof Account) {
            return new AccountPermissionChecker(account, (Account) object);
        }
        throw new RuntimeException();
    }
}