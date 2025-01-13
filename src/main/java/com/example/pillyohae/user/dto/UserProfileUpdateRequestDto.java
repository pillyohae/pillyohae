package com.example.pillyohae.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequestDto {

    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하여야 합니다.")
    private String newName;

    private String newAddress;

    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "비밀번호는 최소 8자 이상이어야 하며, 대소문자, 숫자, 특수문자를 포함해야 합니다"
    )
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다")
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
