package com.app.account_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundAccountRequest {
    @NotNull(message = "Account ID is required")
    private UUID accountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "499.0", message = "Amount must be greater than 499.0")
    @Digits(integer = 10, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    public @NotNull(message = "Account ID is required") UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(@NotNull(message = "Account ID is required") UUID accountId) {
        this.accountId = accountId;
    }

    public @NotNull(message = "Amount is required") @DecimalMin(value = "499.0", message = "Amount must be greater than 499.0") @Digits(integer = 10, fraction = 2, message = "Amount format is invalid") BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull(message = "Amount is required") @DecimalMin(value = "499.0", message = "Amount must be greater than 499.0") @Digits(integer = 10, fraction = 2, message = "Amount format is invalid") BigDecimal amount) {
        this.amount = amount;
    }
}
