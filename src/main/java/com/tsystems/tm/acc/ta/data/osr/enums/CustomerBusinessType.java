package com.tsystems.tm.acc.ta.data.osr.enums;

public enum CustomerBusinessType {

    PRIVATE("PRIVATE"),
    BUSINESS("BUSINESS"),
    WHOLESALE("WHOLESALE"),
    UNDEFINED("UNDEFINED");

    private final String value;

    CustomerBusinessType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
