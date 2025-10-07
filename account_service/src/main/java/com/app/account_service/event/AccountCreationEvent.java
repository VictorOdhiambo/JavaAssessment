package com.app.account_service.event;

import java.util.UUID;

public class AccountCreationEvent {
    private UUID customerId;

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }
}
