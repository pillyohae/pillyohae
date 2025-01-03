package com.example.pillyohae.global.entity;

import lombok.Getter;

@Getter
public enum Role {
    USER("user"),
    ADMIN("admin"),
    SECESSION("deleted");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public static Role of(String roleName) {
        for (Role role : values()) {
            if (role.getName().equals(roleName)) {
                return role;
            }
        }

        throw new IllegalArgumentException("해당하는 이름의 권한을 찾을 수 없습니다: " + roleName);
    }
}
