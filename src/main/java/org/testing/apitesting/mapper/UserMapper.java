package org.testing.apitesting.mapper;

import org.testing.apitesting.domain.User;
import org.testing.apitesting.domain.dto.CreateUserRequest;
import org.testing.apitesting.domain.dto.UserResponse;
import org.testing.apitesting.domain.type.VerificationStatus;

public class UserMapper {

    public static User toEntity(CreateUserRequest request) {
        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .verificationStatus(VerificationStatus.PENDING)
                .build();
    }

    public static UserResponse toResponse(User user) {

       return UserResponse.builder()
               .id(user.getId())
               .email(user.getEmail())
               .fullName(user.getFullName())
               .phoneNumber(user.getPhoneNumber())
               .verificationStatus(user.getVerificationStatus().name())
               .build();

    }
}
