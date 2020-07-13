package com.tsystems.tm.acc.ta.data.osr.enums;

public enum DevicePortLifeCycleStateUI {
    INSTALLING("in Konfiguration"),
    NOTOPERATING("außer Betrieb"),
    OPERATING("in Betrieb"),
    PLANNING("in Planung"),
    RETIRING("in Dekonfiguration");
    private final String value;

    DevicePortLifeCycleStateUI(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}