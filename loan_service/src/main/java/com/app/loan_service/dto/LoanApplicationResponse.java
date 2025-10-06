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
public class LoanApplicationResponse {
    private Long loanApplicationId;
    private Long accountId;
    private BigDecimal loanAmount;
    private Integer tenureMonths;
    private String status;
    private LoanScheduleResponse schedule;
    private String message;
}
