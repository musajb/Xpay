package org.testing.apitesting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.testing.apitesting.domain.User;
import org.testing.apitesting.domain.dto.*;
import org.testing.apitesting.domain.type.VerificationStatus;
import org.testing.apitesting.exception.UserAlreadyExistsException;
import org.testing.apitesting.exception.UserNotFoundException;
import org.testing.apitesting.mapper.UserMapper;
import org.testing.apitesting.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    public AuthenticationResponse register(CreateUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new UserAlreadyExistsException("User with phone number " + request.getPhoneNumber() + " already exists");
        }
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .verificationStatus(VerificationStatus.PENDING)
                .verified(false)
                .build();

        User savedUser = userRepository.save(user);
        otpService.generateAndSendOtp(user.getPhoneNumber());

        return AuthenticationResponse.builder()
                .message("User registered successfully. Please verify OTP sent to your phone.")
                .userResponse(UserMapper.toResponse(savedUser))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.getIdentifier())
                .or(() -> userRepository.findByPhoneNumber(request.getIdentifier()))
                .orElseThrow(() -> new UserNotFoundException("User not found with identifier: " + request.getIdentifier()));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        request.getPassword()
                )
        );

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userResponse(UserMapper.toResponse(user))
                .build();
    }

    public AuthenticationResponse setPasscode(SetPasscodeRequest request) {
        var user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new UserNotFoundException("User not found with phone number: " + request.getPhoneNumber()));

        if (!user.isVerified()) {
            throw new IllegalStateException("User phone number not verified. Please verify OTP first.");
        }

        user.setPassword(passwordEncoder.encode(request.getPasscode()));
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .message("Passcode set successfully.")
                .token(jwtToken)
                .userResponse(UserMapper.toResponse(user))
                .build();
    }
}