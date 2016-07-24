package ru.phi.modules.entity;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    admin,
    user,
    content,
    device;


    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
