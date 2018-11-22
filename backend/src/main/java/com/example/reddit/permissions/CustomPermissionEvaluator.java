package com.example.reddit.permissions;

import com.example.reddit.model.Account;
import com.example.reddit.model.Comment;
import com.example.reddit.model.Post;
import com.example.reddit.model.Group;
import com.example.reddit.permissions.checker.*;
import com.example.reddit.security.UserPrincipal;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
        throw new NotImplementedException();
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
        throw new NotImplementedException();
    }
}