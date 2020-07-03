package com.tsystems.tm.acc.ta.data.osr.enums;

public enum SourceType {
    VVM("VVM"),
    BLW("BLW");

    private String value;

    SourceType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
