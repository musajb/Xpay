package org.testing.apitesting.service;

import lombok.RequiredArgsConstructor;
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
    private final WalletService walletService;

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

        // Check wallet balance
        Double balance = walletService.getOrCreateCurrentUserWallet().getBalance();
        if (balance < request.getAmount()) {
            return AirtimeResponse.builder()
                    .status("FAILED")
                    .message("Insufficient balance. Your balance is: " + balance)
                    .pinRequired(false)
                    .build();
        }

        // Default to config or fetch from request
        try {
            africaStalkingService.sendAirtime(request.getPhoneNumber(), request.getAmount(), request.getCurrency());
            
            String reference = UUID.randomUUID().toString();
            walletService.debitForAirtime(user, request.getAmount(), request.getPhoneNumber(), reference, true);

            return AirtimeResponse.builder()
                    .status("SUCCESS")
                    .message("Airtime purchase initiated successfully")
                    .pinRequired(true)
                    .recipient(request.getPhoneNumber())
                    .transactionReference(reference)
                    .build();
        } catch (Exception e) {
            String reference = UUID.randomUUID().toString();
            walletService.debitForAirtime(user, request.getAmount(), request.getPhoneNumber(), reference, false);
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

    public  Map<String, Object> initiateAirtimePurchase() {
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
