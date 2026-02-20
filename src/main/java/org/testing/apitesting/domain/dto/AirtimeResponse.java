package org.testing.apitesting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirtimeResponse {
    private String status;
    private String message;
    private boolean pinRequired;
    private String transactionReference;
    private String recipient;
}
