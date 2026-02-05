package org.testing.apitesting.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    private static final String AFRICA_TALKING_URL =
            "https://api.sandbox.africastalking.com/version1/messaging";

    @Value("${africastalking.username}")
    private String username;

    @Value("${africastalking.api-key}")
    private String apiKey;

    @Value("${africastalking.sender-id}")
    private String senderId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendSms(String phoneNumber, String message) {
        // Validate inputs
        if ( phoneNumber == null || phoneNumber.isEmpty() ) {
            log.error("Phone number is empty");
            return;
        }

        if ( message == null || message.isEmpty() ) {
            log.error("Message is empty");
            return;
        }

        // Log what we're sending
        log.info("Attempting to send SMS to: {}", phoneNumber);
        log.info("Username: {}", username);
        log.info("Sender ID: {}", senderId);
        log.info("API Key present: {}", apiKey != null && !apiKey.isEmpty());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("apiKey", apiKey);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", username);
        body.add("to", phoneNumber); // Must be in format: +234... or +254...
        body.add("message", message);

        // Optional: Only add 'from' if you have an approved sender ID
        if ( senderId != null && !senderId.isEmpty() ) {
            body.add("from", senderId);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    AFRICA_TALKING_URL,
                    request,
                    String.class
            );

            // Check if successful
            if ( response.getStatusCode().is2xxSuccessful() ) {
                log.info("SMS sent successfully to {}", phoneNumber);
            } else {
                log.error("SMS failed with status: {}", response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            log.error("SMS Client Error: Status={}, Body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception ex) {
            log.error("Failed to send SMS: {}", ex.getMessage(), ex);
        }
    }
}

