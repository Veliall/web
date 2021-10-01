package org.example.app.util;

import org.example.framework.security.Roles;

import java.util.ArrayList;
import java.util.List;

public class RolesConverter {
    private RolesConverter(){}

    public static List<String> readRoles(String[] roles) {
        final var result = new ArrayList<String>();
        for (String role : roles) {
            switch (role) {
                case "admin" -> result.add(Roles.ROLE_ADMIN);
                case "client" -> result.add(Roles.ROLE_CLIENT);
            }
        }
        return result;
    }
}
