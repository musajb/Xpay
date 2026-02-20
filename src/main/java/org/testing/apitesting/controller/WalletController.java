package org.testing.apitesting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.testing.apitesting.domain.dto.WalletResponse;
import org.testing.apitesting.service.WalletService;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/virtual-account")
    public ResponseEntity<WalletResponse> generateVA() {
        return ResponseEntity.ok(walletService.generateVirtualAccount());
    }
}
