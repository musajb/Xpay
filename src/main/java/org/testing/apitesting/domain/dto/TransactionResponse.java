package org.testing.apitesting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.testing.apitesting.domain.type.TransactionStatus;
import org.testing.apitesting.domain.type.TransactionType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Double amount;
    private TransactionType type;
    private TransactionStatus status;
    private String reference;
    private String recipient;
    private String description;
    private LocalDateTime createdAt;
}
