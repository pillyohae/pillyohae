package com.example.pillyohae.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserDeleteRequestDto {

    private final String password;

    public UserDeleteRequestDto(@JsonProperty("password") String password) {
        this.password = password;
    }
}
