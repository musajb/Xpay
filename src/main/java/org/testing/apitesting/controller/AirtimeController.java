package org.testing.apitesting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.testing.apitesting.domain.dto.AirtimeRequest;
import org.testing.apitesting.domain.dto.AirtimeResponse;
import org.testing.apitesting.domain.dto.AirtimeSummaryResponse;
import org.testing.apitesting.service.AirtimeService;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/airtime")
@RequiredArgsConstructor
public class AirtimeController {

    private final AirtimeService airtimeService;

    @GetMapping("/initiate")
    public ResponseEntity<Map<String, Object>> initiate() {

        Map<String, Object> initiateAirtime = airtimeService.initiateAirtimePuchase();

        return ResponseEntity.ok(initiateAirtime);
    }

    @PostMapping("/summary")
    public ResponseEntity<AirtimeSummaryResponse> summary(@RequestBody AirtimeRequest request) {
        return ResponseEntity.ok(airtimeService.getSummary(request));
    }

    @PostMapping("/purchase")
    public ResponseEntity<AirtimeResponse> purchase(@RequestBody AirtimeRequest request) {
        return ResponseEntity.ok(airtimeService.purchaseAirtime(request));
    }

    @PostMapping("/transaction-pin")
    public ResponseEntity<Void> setPin(@RequestParam String pin, @AuthenticationPrincipal UserDetails userDetails) {
        airtimeService.setTransactionPin(pin, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
