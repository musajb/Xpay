package org.testing.apitesting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody CreateUserRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/set-passcode")
    public ResponseEntity<AuthenticationResponse> setPasscode(
            @RequestBody SetPasscodeRequest request
    ) {
        return ResponseEntity.ok(service.setPasscode(request));
    }
}