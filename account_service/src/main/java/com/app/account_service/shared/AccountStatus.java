package com.app.account_service.shared;

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
