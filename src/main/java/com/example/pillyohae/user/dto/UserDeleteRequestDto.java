package com.example.pillyohae.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserDeleteRequestDto {

    @NotBlank(message = "패스워드를 입력해주세요")
    private final String password;

    public UserDeleteRequestDto(@JsonProperty("password") String password) {
        this.password = password;
    }
}
