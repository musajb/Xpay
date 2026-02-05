package org.testing.apitesting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.testing.apitesting.domain.Otp;
import org.testing.apitesting.repository.OtpRepository;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final SmsService smsService;

    public void generateAndSendOtp(String phoneNumber) {

        String code = String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 999999)
        );

        otpRepository.save(
                Otp.builder()
                        .phoneNumber(phoneNumber)
                        .code(code)
                        .expiresAt(LocalDateTime.now().plusMinutes(5))
                        .build()
        );

        smsService.sendSms(
                phoneNumber,
                "Your OTP code is: " + code
        );
    }
}

