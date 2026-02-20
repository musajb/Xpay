package org.testing.apitesting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.testing.apitesting.domain.dto.ApiResponse;
import org.testing.apitesting.domain.dto.WalletResponse;
import org.testing.apitesting.service.MonnifyService;
import org.testing.apitesting.service.WalletService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final MonnifyService monnifyService;

    @PostMapping("/virtual-account")
    public ResponseEntity<ApiResponse<WalletResponse>> generateVA() {
        return ResponseEntity.ok(ApiResponse.success("Virtual account generated successfully", walletService.generateVirtualAccount()));
    }

    @GetMapping("/va-balance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVirtualAccountBalance() {
        return ResponseEntity.ok(ApiResponse.success("Balance retrieved successfully", walletService.getVirtualAccountBalance()));
    }

}
