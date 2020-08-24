package com.tsystems.tm.acc.ta.data.osr.enums;

public enum AccessLineStatus {
    INACTIVE("INACTIVE"),
    WALLED_GARDEN("WALLED_GARDEN"),
    ASSIGNED("ASSIGNED");

    private final String value;

    AccessLineStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
