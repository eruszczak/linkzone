package pl.reryk.linkzone.permissions;

import pl.reryk.linkzone.model.Account;
import pl.reryk.linkzone.model.Comment;
import pl.reryk.linkzone.model.Group;
import pl.reryk.linkzone.model.Post;
import pl.reryk.linkzone.security.UserPrincipal;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import pl.reryk.linkzone.permissions.checker.*;

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