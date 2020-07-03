package com.tsystems.tm.acc.ta.data.osr.enums;

public enum PropertyOwnerClassificationType {
    PROPERTY_OWNER("PROPERTY_OWNER"),
    PROPERTY_MANAGER("PROPERTY_MANAGER"),
    NOT_DEFINED("NOT_DEFINED");

    private String value;

    PropertyOwnerClassificationType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
