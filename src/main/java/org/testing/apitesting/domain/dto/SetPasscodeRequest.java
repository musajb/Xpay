package org.testing.apitesting.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SetPasscodeRequest {
    @NotBlank
    @Pattern(
            regexp = "^\\+?[1-9]\\d{9,14}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;

    @NotBlank
    private String passcode;
}
