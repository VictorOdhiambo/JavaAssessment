package com.app.loan_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentDetail {
    private Integer installmentNumber;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal totalAmount;
    private BigDecimal remainingBalance;
}
