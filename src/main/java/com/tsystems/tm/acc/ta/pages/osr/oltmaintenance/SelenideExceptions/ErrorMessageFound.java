package com.tsystems.tm.acc.ta.pages.osr.oltmaintenance.SelenideExceptions;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.UIAssertionError;

public class ErrorMessageFound extends UIAssertionError {
    public ErrorMessageFound(String errorMessage) {
        super(WebDriverRunner.driver(), "Error message found: " + errorMessage);
    }
}