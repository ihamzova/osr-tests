package com.tsystems.tm.acc.ta.data.osr.enums;

public enum CustomerType {

    INDIVIDUAL("INDIVIDUAL"),
    ORGANIZATION("ORGANIZATION");

    private final String value;

    CustomerType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
