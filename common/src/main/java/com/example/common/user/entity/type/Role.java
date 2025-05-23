package com.example.common.user.entity.type;

import lombok.Generated;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public enum Role {
    BUYER("buyer"),
    SELLER("seller"),
    ADMIN("admin");

    private final String name;

    public static Role of(String roleName) throws IllegalArgumentException {
        for(Role role : values()) {
            if (role.getName().equals(roleName.toLowerCase())) {
                return role;
            }
        }

        throw new IllegalArgumentException("해당하는 이름의 권한을 찾을 수 없습니다: " + roleName);
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.name()));
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    private Role(final String name) {
        this.name = name;
    }
}

