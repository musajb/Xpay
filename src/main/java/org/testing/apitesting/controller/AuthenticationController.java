package org.testing.apitesting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.testing.apitesting.domain.dto.ApiResponse;
import org.testing.apitesting.domain.dto.AuthenticationRequest;
import org.testing.apitesting.domain.dto.AuthenticationResponse;
import org.testing.apitesting.domain.dto.CreateUserRequest;
import org.testing.apitesting.domain.dto.SetPasscodeRequest;
import org.testing.apitesting.service.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Registration successful", service.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", service.authenticate(request)));
    }

    @PostMapping("/set-passcode")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> setPasscode(@RequestBody SetPasscodeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Passcode set successfully", service.setPasscode(request)));
    }
}