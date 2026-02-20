package org.testing.apitesting.service;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonnifyService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${monnify.base-url:https://sandbox.monnify.com}")
    private String baseUrl;

    @Value("${monnify.api-key:}")
    private String apiKey;

    @Value("${monnify.secret-key:}")
    private String secretKey;

    @Value("${monnify.contract-code:}")
    private String contractCode;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getAccessToken() {
        if (apiKey == null || apiKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            log.error("Monnify API Key or Secret Key is not configured.");
            throw new RuntimeException("Monnify API Key or Secret Key is not configured.");
        }



        String authString = apiKey + ":" + secretKey;
        String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        log.info("Requesting Monnify access token from: {}", baseUrl + "/api/v1/auth/login");
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/v1/auth/login",
                entity,
                AuthResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().getResponseBody().getAccessToken();
        }
        throw new RuntimeException("Failed to obtain Monnify access token: " + response.getStatusCode());
    }

    public ReservedAccountResult createReservedAccount(String accountReference, String accountName, String customerEmail) {
        log.info("Creating Monnify reserved account: reference={}, email={}, contractCode={}", 
                accountReference, customerEmail, contractCode);
        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountReference", accountReference);
        requestBody.put("accountName", accountName);
        requestBody.put("currencyCode", "NGN");
        requestBody.put("contractCode", contractCode);
        requestBody.put("customerEmail", customerEmail);
        requestBody.put("customerName", accountName);
        requestBody.put("getAllAvailableBanks", true);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<ReservedAccountResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/v1/bank-transfer/reserved-accounts",
                entity,
                ReservedAccountResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().isRequestSuccessful()) {
            ReservedAccountResult result = response.getBody().getResponseBody();
            if (result.getAccounts() != null && !result.getAccounts().isEmpty()) {
                ReservedAccountResult.AccountDetails account = result.getAccounts().get(0);
                result.setAccountNumber(account.getAccountNumber());
                result.setBankName(account.getBankName());
            }
            return result;
        }
        throw new RuntimeException("Failed to create Monnify reserved account: " + response.getStatusCode());
    }

    @Data
    public static class AuthResponse {
        private boolean requestSuccessful;
        private String responseMessage;
        private String responseCode;
        private AuthBody responseBody;

        @Data
        public static class AuthBody {
            private String accessToken;
            private int expiresIn;
        }
    }

    @Data
    public static class ReservedAccountResponse {
        private boolean requestSuccessful;
        private String responseMessage;
        private String responseCode;
        private ReservedAccountResult responseBody;
    }

    @Data
    public static class ReservedAccountResult {
        private String accountReference;
        private String accountName;
        private String customerEmail;
        private String accountNumber;
        private String bankName;
        private java.util.List<AccountDetails> accounts;

        @Data
        public static class AccountDetails {
            private String bankCode;
            private String bankName;
            private String accountNumber;
        }
    }
}
