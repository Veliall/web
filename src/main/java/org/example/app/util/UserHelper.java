package org.example.app.util;

import jakarta.servlet.http.HttpServletRequest;
import org.example.app.domain.User;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.Authentication;

import java.util.Collection;
import java.util.regex.Matcher;

public class UserHelper {
    private UserHelper() {
    }

    public static User getUser(HttpServletRequest req) {
        return ((User) ((Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR)).getPrincipal());
    }

    public static Collection<String> getAuthorities(HttpServletRequest req) {
        return ((Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR))
                .getAuthorities();
    }

    public static long getUserId(HttpServletRequest req) {
        return Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR))
                .group("userId"));
    }
}
