package com.app.loan_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "banking.loan")
public class LoanProperties {
    private double minFundLimit;
    private double minAmount;
    private double maxAmount;

    public double getMinFundLimit() {
        return minFundLimit;
    }

    public void setMinFundLimit(double minFundLimit) {
        this.minFundLimit = minFundLimit;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(double minAmount) {
        this.minAmount = minAmount;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }
}
