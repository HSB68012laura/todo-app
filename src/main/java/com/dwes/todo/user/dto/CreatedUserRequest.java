package com.dwes.todo.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CreatedUserRequest {

    private String username, password, verifyPassword, email, fullname;

    public boolean isPasswordMatching() {
        return password != null && password.equals(verifyPassword);
    }

    public boolean isPasswordStrong() {
        if (password == null) return false;
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[0-9].*");
    }
}
