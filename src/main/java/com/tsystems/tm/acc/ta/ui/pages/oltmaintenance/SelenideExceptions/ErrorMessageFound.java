package com.tsystems.tm.acc.ta.ui.pages.oltmaintenance.SelenideExceptions;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.UIAssertionError;

public class ErrorMessageFound extends UIAssertionError {
    public ErrorMessageFound(String errorMessage) {
        super(WebDriverRunner.driver(), "Error message found: " + errorMessage);
    }

    public String toString() {
        return this.getClass().getSimpleName() + ' ' + this.getMessage() + this.uiDetails();
    }
}