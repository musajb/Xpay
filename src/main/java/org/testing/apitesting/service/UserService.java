package org.testing.apitesting.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.testing.apitesting.domain.User;
import org.testing.apitesting.domain.dto.CreateUserRequest;
import org.testing.apitesting.domain.dto.UpdateUserRequest;
import org.testing.apitesting.domain.dto.UserResponse;
import org.testing.apitesting.exception.UserAlreadyExistsException;
import org.testing.apitesting.exception.UserNotFoundException;
import org.testing.apitesting.mapper.UserMapper;
import org.testing.apitesting.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(CreateUserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new UserAlreadyExistsException("User with phone number " + request.getPhoneNumber() + " already exists");
        }

        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);

        otpService.generateAndSendOtp(savedUser.getPhoneNumber());

        return UserMapper.toResponse(savedUser);
    }

    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        User updatedUser = userRepository.save(user);

        return UserMapper.toResponse(updatedUser);
    }

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
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
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
