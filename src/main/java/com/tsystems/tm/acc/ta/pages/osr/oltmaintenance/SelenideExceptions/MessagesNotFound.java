package com.tsystems.tm.acc.ta.pages.osr.oltmaintenance.SelenideExceptions;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.UIAssertionError;

public class MessagesNotFound extends UIAssertionError {
    public MessagesNotFound() {
        super(WebDriverRunner.driver(), "Not all message not were found");
    }
}