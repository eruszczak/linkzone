package com.example.reddit.utils;

import com.example.reddit.exception.NoPermissionsException;
import com.example.reddit.model.Account;
import com.github.slugify.Slugify;
import org.springframework.security.core.userdetails.UserDetails;

public class Utils {

    public static void checkIfOwner(UserDetails principal, String username) {
        if (principal == null || !principal.getUsername().equals(username)) {
            throw new NoPermissionsException();
        }
    }

    public static String getSlug(String value) {
        final int maxLength = 200;
        Slugify slg = new Slugify();
        String slug = slg.slugify(value);
        return slug.length() > maxLength ? slug.substring(0, maxLength) : slug;
    }
}
