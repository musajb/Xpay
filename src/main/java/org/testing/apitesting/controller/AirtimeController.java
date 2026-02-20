package org.testing.apitesting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.testing.apitesting.domain.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<Map<String, Object>>> initiate() {

        Map<String, Object> initiateAirtime = airtimeService.initiateAirtimePurchase();

        return ResponseEntity.ok(ApiResponse.success("Airtime purchase initiated", initiateAirtime));
    }

    @PostMapping("/summary")
    public ResponseEntity<ApiResponse<AirtimeSummaryResponse>> summary(@RequestBody AirtimeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Airtime summary retrieved", airtimeService.getSummary(request)));
    }

    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<AirtimeResponse>> purchase(@RequestBody AirtimeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Airtime purchased successfully", airtimeService.purchaseAirtime(request)));
    }
}
