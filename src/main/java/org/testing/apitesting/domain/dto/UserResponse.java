package org.testing.apitesting.domain.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String verificationStatus;
}