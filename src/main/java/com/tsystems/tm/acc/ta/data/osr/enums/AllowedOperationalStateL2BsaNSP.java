package com.tsystems.tm.acc.ta.data.osr.enums;

public enum AllowedOperationalStateL2BsaNSP {
    WORKING("WORKING"),
    NOT_WORKING("NOT_WORKING"),
    INSTALLING("INSTALLING"),
    FAILED("FAILED"),
    ACTIVATING("ACTIVATING"),
    DEACTIVATING("DEACTIVATING"),
    NOT_MANAGEABLE("NOT_MANAGEABLE");

    private final String value;

    AllowedOperationalStateL2BsaNSP(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
