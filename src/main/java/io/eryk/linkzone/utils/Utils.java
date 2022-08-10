package io.eryk.linkzone.utils;

import com.github.slugify.Slugify;
import io.eryk.linkzone.exception.UnauthorizedException;
import org.springframework.security.core.userdetails.UserDetails;

public class Utils {

    public static void checkIfOwner(UserDetails principal, String username) {
        if (principal == null || !principal.getUsername().equals(username)) {
            throw new UnauthorizedException();
        }
    }

    public static void checkIfAuthenticated(UserDetails principal) {
        if (principal == null) {
            throw new UnauthorizedException();
        }
    }

    public static String getSlug(String value) {
        final int maxLength = 200;
        final Slugify slg = Slugify.builder().build();
        String slug = slg.slugify(value);
        return slug.length() > maxLength ? slug.substring(0, maxLength) : slug;
    }
}
