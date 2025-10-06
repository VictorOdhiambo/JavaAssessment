package com.app.loan_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequest {
    @NotNull(message = "Account ID is required")
    @Positive(message = "Account ID must be positive")
    private Long accountId;

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "1.0", message = "Loan amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Loan amount format is invalid")
    private BigDecimal loanAmount;

    @NotNull(message = "Tenure is required")
    @Min(value = 1, message = "Tenure must be at least 1 month")
    @Max(value = 60, message = "Tenure cannot exceed 60 months")
    private Integer tenureMonths;
}
