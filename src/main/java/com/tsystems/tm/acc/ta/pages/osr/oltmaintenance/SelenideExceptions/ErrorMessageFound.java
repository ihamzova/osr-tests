package com.tsystems.tm.acc.ta.pages.osr.oltmaintenance.SelenideExceptions;

import com.codeborne.selenide.ex.UIAssertionError;

public class ErrorMessageFound extends UIAssertionError {
    public ErrorMessageFound(String errorMessage) {
        super("Error message found: " + errorMessage);
    }
}