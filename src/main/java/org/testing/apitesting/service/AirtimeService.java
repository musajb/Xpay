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
import org.testing.apitesting.domain.type.TransactionStatus;
import org.testing.apitesting.domain.type.TransactionType;
import org.testing.apitesting.exception.UserNotFoundException;
import org.testing.apitesting.repository.TransactionRepository;
import org.testing.apitesting.repository.UserRepository;
import org.testing.apitesting.domain.Transaction;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AirtimeService {

    private final AfricaStalkingService africaStalkingService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionRepository transactionRepository;

    public AirtimeResponse purchaseAirtime(AirtimeRequest request) {
        User user = getAuthenticatedUser();

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
        try {
            africaStalkingService.sendAirtime(request.getPhoneNumber(), request.getAmount(), "NGN");
            
            String reference = UUID.randomUUID().toString();
            Transaction transaction = Transaction.builder()
                    .user(user)
                    .amount(request.getAmount())
                    .type(TransactionType.AIRTIME)
                    .status(TransactionStatus.SUCCESS)
                    .reference(reference)
                    .recipient(request.getPhoneNumber())
                    .description("Airtime purchase to " + request.getPhoneNumber())
                    .createdAt(LocalDateTime.now())
                    .build();
            transactionRepository.save(transaction);

            return AirtimeResponse.builder()
                    .status("SUCCESS")
                    .message("Airtime purchase initiated successfully")
                    .pinRequired(false)
                    .transactionReference(reference)
                    .build();
        } catch (Exception e) {
            String reference = UUID.randomUUID().toString();
            Transaction transaction = Transaction.builder()
                    .user(user)
                    .amount(request.getAmount())
                    .type(TransactionType.AIRTIME)
                    .status(TransactionStatus.FAILED)
                    .reference(reference)
                    .recipient(request.getPhoneNumber())
                    .description("Failed Airtime purchase to " + request.getPhoneNumber())
                    .createdAt(LocalDateTime.now())
                    .build();
            transactionRepository.save(transaction);
            
            return AirtimeResponse.builder()
                    .status("FAILED")
                    .message("Airtime purchase failed: " + e.getMessage())
                    .pinRequired(false)
                    .transactionReference(reference)
                    .build();
        }
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
        User user = getAuthenticatedUser();

        Map<String, Object> response = new HashMap<>();
        response.put("phoneNumber", user.getPhoneNumber());
        response.put("pinSet", user.getTransactionPin() != null);

        return response;
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
