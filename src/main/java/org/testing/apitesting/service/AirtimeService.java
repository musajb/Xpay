package org.testing.apitesting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.testing.apitesting.domain.User;
import org.testing.apitesting.domain.dto.AirtimeRequest;
import org.testing.apitesting.domain.dto.AirtimeResponse;
import org.testing.apitesting.domain.dto.AirtimeSummaryResponse;
import org.testing.apitesting.exception.UserNotFoundException;
import org.testing.apitesting.repository.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AirtimeService {

    private final AfricaStalkingService africaStalkingService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AirtimeResponse purchaseAirtime(AirtimeRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getTransactionPin() == null) {
            return AirtimeResponse.builder()
                    .status("FAILED")
                    .message("Transaction PIN not set")
                    .pinRequired(true)
                    .build();
        }

        if (request.getTransactionPin() == null || request.getTransactionPin().isEmpty()) {
             return AirtimeResponse.builder()
                    .status("FAILED")
                    .message("Transaction PIN required")
                    .pinRequired(false)
                    .build();
        }

        if (!passwordEncoder.matches(request.getTransactionPin(), user.getTransactionPin())) {
            return AirtimeResponse.builder()
                    .status("FAILED")
                    .message("Invalid Transaction PIN")
                    .pinRequired(false)
                    .build();
        }

        // Default to NGN for now, or fetch from config
        africaStalkingService.sendAirtime(request.getPhoneNumber(), request.getAmount(), "NGN");

        return AirtimeResponse.builder()
                .status("SUCCESS")
                .message("Airtime purchase initiated successfully")
                .pinRequired(false)
                .build();
    }


   public void setTransactionPin(String pin, String email) {

       if (!pin.matches("\\d{4}")) {
           throw new IllegalArgumentException("PIN must be 4 digits");
       }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

       if (user.getTransactionPin() != null) {
           throw new IllegalStateException("Transaction PIN already set");
       }

        user.setTransactionPin(passwordEncoder.encode(pin));
        userRepository.save(user);
    }

    public AirtimeSummaryResponse getSummary(AirtimeRequest request) {
        // Here we could implement network detection logic
        return AirtimeSummaryResponse.builder()
                .phoneNumber(request.getPhoneNumber())
                .amount(request.getAmount())
                .networkProvider("Detected Network") // Placeholder
                .fee(0.0)
                .build();
    }

    public  Map<String, Object> initiateAirtimePuchase() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("phoneNumber", user.getPhoneNumber());
        response.put("pinSet", user.getTransactionPin() != null);

        return response;
    }
}
