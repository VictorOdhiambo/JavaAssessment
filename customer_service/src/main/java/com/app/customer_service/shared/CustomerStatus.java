package com.app.customer_service.shared;

public enum CustomerStatus {
    PENDING_VERIFICATION(0),
    ACTIVE(1);

    final int value;
    CustomerStatus(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
