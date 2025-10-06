package com.app.customer_service.shared;

public enum AccountStatus {
    ACTIVE(1),
    INACTIVE(2);

    final int value;
    AccountStatus(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
