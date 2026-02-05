package org.testing.apitesting.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest{

        @NotBlank(message = "Full name is required")
        private String fullName;

        @Email(message = "Invalid email")
        @NotBlank(message = "Email is required")
        private String email;
}