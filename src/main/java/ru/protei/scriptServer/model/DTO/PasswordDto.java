package ru.protei.scriptServer.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDto {
    private String oldPassword;

    private String token;

    private String newPassword;
}
