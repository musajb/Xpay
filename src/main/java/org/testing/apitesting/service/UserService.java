package org.testing.apitesting.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.testing.apitesting.domain.User;
import org.testing.apitesting.domain.dto.CreateUserRequest;
import org.testing.apitesting.domain.dto.UpdateUserRequest;
import org.testing.apitesting.domain.dto.UserResponse;
import org.testing.apitesting.exception.ResourceNotFoundException;
import org.testing.apitesting.mapper.UserMapper;
import org.testing.apitesting.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OtpService otpService;

    public UserResponse create(CreateUserRequest request) {
        User user = UserMapper.toEntity(request);
        User savedUser = userRepository.save(user);

        otpService.generateAndSendOtp(savedUser.getPhoneNumber());

        return UserMapper.toResponse(savedUser);
    }

    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        User updatedUser = userRepository.save(user);

        return UserMapper.toResponse(updatedUser);
    }

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}
