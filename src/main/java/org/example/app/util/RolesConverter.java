package org.example.app.util;

import org.example.framework.security.Roles;

import java.util.ArrayList;
import java.util.List;

public class RolesConverter {
    private RolesConverter(){}

    public static List<String> readRoles(Integer[] roles) {
        final var result = new ArrayList<String>();
        for (int role : roles) {
            switch (role) {
                case 1 -> result.add(Roles.ROLE_ADMIN);
                case 2 -> result.add(Roles.ROLE_CLIENT);
            }
        }
        return result;
    }
}
