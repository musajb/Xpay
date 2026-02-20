package org.testing.apitesting.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletResponse {
    private Double balance;
    private String vaAccountNumber;
    private String vaBankName;
    private String vaReference;
}
