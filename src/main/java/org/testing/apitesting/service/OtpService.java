package org.testing.apitesting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.testing.apitesting.domain.Otp;
import org.testing.apitesting.domain.User;
import org.testing.apitesting.exception.ResourceNotFoundException;
import org.testing.apitesting.repository.OtpRepository;
import org.testing.apitesting.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final SmsService smsService;
    private final UserRepository userRepository;

    public void generateAndSendOtp(String phoneNumber) {

        String code = String.valueOf(
                ThreadLocalRandom.current().nextInt(1000, 9999)
        );

        otpRepository.save(
                Otp.builder()
                        .phoneNumber(phoneNumber)
                        .code(code)
                        .expiresAt(LocalDateTime.now().plusMinutes(5))
                        .build()
        );

        smsService.sendSms(phoneNumber, "Your OTP code is: " + code
        );
    }

    public void verifyOtp(String phoneNumber, String code) {

        Otp otp = otpRepository.findByPhoneNumberAndCodeAndUsedFalse(phoneNumber, code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        if (otp.isExpired()) {
            otpRepository.delete(otp);
            throw new IllegalStateException("OTP expired");
        }

        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

        user.verify();
        otp.markUsed();

        // optional hard delete instead of used=true
        otpRepository.delete(otp);
    }
}

