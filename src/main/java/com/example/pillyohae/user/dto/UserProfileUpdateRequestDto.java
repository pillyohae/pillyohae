package com.example.pillyohae.user.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequestDto {

    private String newName;
    private String newAddress;
    private String newPassword;

    @NotBlank(message = "기존 비밀번호는 필수 입력 값입니다.")
    private String password;

    public Map<String, Object> toNonNullFields() {
        Map<String, Object> nonNullFields = new HashMap<>();
        if (newName != null) {
            nonNullFields.put("name", newName);
        }
        if (newAddress != null) {
            nonNullFields.put("address", newAddress);
        }
        if (newPassword != null) {
            nonNullFields.put("password", newPassword);
        }
        return nonNullFields;
    }

}
