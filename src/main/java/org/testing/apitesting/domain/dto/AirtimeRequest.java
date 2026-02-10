package org.testing.apitesting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirtimeRequest {
    private String phoneNumber;
    private Double amount;
    private String transactionPin;
}
