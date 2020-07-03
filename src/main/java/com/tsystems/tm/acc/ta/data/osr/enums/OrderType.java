package com.tsystems.tm.acc.ta.data.osr.enums;

public enum OrderType {
    FIBER_ACCESS_APARTMENT("FIBER_ACCESS_APARTMENT"),
    FIBER_ACCESS_BUILDING("FIBER_ACCESS_BUILDING");

    private String value;

    OrderType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
