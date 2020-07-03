package com.tsystems.tm.acc.ta.data.osr.enums;

public enum Salutation {
    MR("Herr"),
    MRS("Frau"),
    COMPANY("COMPANY"),
    NOT_DEFINED("NOT_DEFINED");

    private String value;

    Salutation(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
