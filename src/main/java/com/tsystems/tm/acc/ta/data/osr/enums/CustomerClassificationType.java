package com.tsystems.tm.acc.ta.data.osr.enums;

public enum CustomerClassificationType {

    CUSTOMER_IS_PROPERTYOWNER("CUSTOMER_IS_PROPERTYOWNER"),
    CUSTOMER_IS_PARTIAL_PROPERTYOWNER("CUSTOMER_IS_PARTIAL_PROPERTYOWNER"),
    CUSTOMER_IS_TENANT("CUSTOMER_IS_TENANT"),
    NOT_DEFINED("NOT_DEFINED");

    private final String value;

    CustomerClassificationType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
