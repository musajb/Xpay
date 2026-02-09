package org.testing.apitesting.domain.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateUserRequest{

        @NotBlank
        private String fullName;

        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;

        @Pattern(
                regexp = "^\\+?[1-9]\\d{9,14}$",
                message = "Invalid phone number"
        )
        private String phoneNumber;
}