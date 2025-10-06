package com.app.loan_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanScheduleResponse {
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private BigDecimal totalInterest;
    private BigDecimal totalAmount;
    private BigDecimal monthlyPayment;
    private List<InstallmentDetail> installments;
}
