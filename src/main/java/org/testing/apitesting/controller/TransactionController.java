package org.testing.apitesting.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.testing.apitesting.domain.dto.TransactionResponse;
import org.testing.apitesting.domain.type.TransactionType;
import org.testing.apitesting.service.TransactionService;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/transactions")
    public Page<TransactionResponse> getTransactions(
            @RequestParam(required = true) Long userid,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return transactionService.getUserTransactions(userid, type, page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@RequestParam Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getById(userId, id));
    }
}
