package org.testing.apitesting.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.testing.apitesting.domain.dto.ApiResponse;
import org.testing.apitesting.domain.dto.TransactionResponse;
import org.testing.apitesting.domain.type.TransactionType;
import org.testing.apitesting.service.TransactionService;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/transactions")
    public ApiResponse<Page<TransactionResponse>> getTransactions(
            @RequestParam(required = true) Long userid,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success("Transactions retrieved successfully", transactionService.getUserTransactions(userid, type, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getById(@RequestParam Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", transactionService.getById(userId, id)));
    }
}
