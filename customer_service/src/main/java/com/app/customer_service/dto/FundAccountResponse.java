package com.app.customer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundAccountResponse {
    private Long accountId;
    private String accountNumber;
    private BigDecimal newBalance;
    private String currency;
    private String message;
}
