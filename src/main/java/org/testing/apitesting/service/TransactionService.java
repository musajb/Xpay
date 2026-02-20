package org.testing.apitesting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.testing.apitesting.domain.Transaction;
import org.testing.apitesting.domain.dto.TransactionResponse;
import org.testing.apitesting.domain.type.TransactionType;
import org.testing.apitesting.exception.ResourceNotFoundException;
import org.testing.apitesting.repository.TransactionRepository;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Page<TransactionResponse> getUserTransactions(
            Long userId,
            TransactionType type,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactions = (type == null)
                ? transactionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                : transactionRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);

        return transactions.map(this::mapToResponse);
    }

    public TransactionResponse getById(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository
                .findByIdAndUserId(transactionId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction not found")
                );

        return mapToResponse(transaction);
    }


    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .reference(transaction.getReference())
                .recipient(transaction.getRecipient())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
