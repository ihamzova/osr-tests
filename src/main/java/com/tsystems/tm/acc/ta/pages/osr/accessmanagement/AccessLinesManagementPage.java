package com.tsystems.tm.acc.ta.pages.osr.accessmanagement;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class AccessLinesManagementPage {

    private static final String TITLE = "Access Lines durchsuchen";
    private static final long TIMEOUT_MS = 30000;

    private static final By NE_DEFAULT_PROFILE_STATE_INPUT = byQaData("ne-def-state-input");
    private static final By NE_SUBSCRIBER_PROFILE_TITLE = byXpath("//am-al-ne-profile//span[contains(text(),'Subscriber Profile')]");
    private static final By NL_DEFAULT_PROFILE_STATE_INPUT = byQaData("nl-def-state-input");
    private static final By NE_SUBSCRIBER_PROFILE_STATE_INPUT = byQaData("ne-sub-state-input");
    private static final By NL_SUBSCRIBER_PROFILE_STATE_INPUT = byQaData("nl-sub-state-input");

    SelenideElement neSubscriberProfileTitle = $(NE_SUBSCRIBER_PROFILE_TITLE);
    ElementsCollection neDefaultProfileStateInput = $$(NE_DEFAULT_PROFILE_STATE_INPUT);
    ElementsCollection nlDefaultProfileStateInput = $$(NL_DEFAULT_PROFILE_STATE_INPUT);
    ElementsCollection neSubscriberProfileStateInput = $$(NE_SUBSCRIBER_PROFILE_STATE_INPUT);
    ElementsCollection nlSubscriberProfileStateInput = $$(NL_SUBSCRIBER_PROFILE_STATE_INPUT);

    public String getPageTitle() {
        return TITLE;
    }

    @Step("Get NE default profile state")
    public String getNEDefaultProfileState() {
        String result = "NULL";
        neSubscriberProfileTitle.waitUntil(Condition.visible, TIMEOUT_MS);
        if (neDefaultProfileStateInput.size() > 0) {
            result = neDefaultProfileStateInput.get(0).getValue();
        }
        return result;
    }

    @Step("Get NL default profile state")
    public String getNLDefaultProfileState() {
        String result = "NULL";
        neSubscriberProfileTitle.waitUntil(Condition.visible, TIMEOUT_MS);
        if (nlDefaultProfileStateInput.size() > 0) {
            result = nlDefaultProfileStateInput.get(0).getValue();
        }
        return result;
    }

    @Step("Get NE subscriber profile state")
    public String getNESubscriberProfileState() {
        String result = "NULL";
        neSubscriberProfileTitle.waitUntil(Condition.visible, TIMEOUT_MS);
        if (neSubscriberProfileStateInput.size() > 0) {
            result = neSubscriberProfileStateInput.get(0).getValue();
        }
        return result;
    }

    @Step("Get NL subscriber profile state")
    public String getNLSubscriberProfileState() {
        String result = "NULL";
        neSubscriberProfileTitle.waitUntil(Condition.visible, TIMEOUT_MS);
        if (nlSubscriberProfileStateInput.size() > 0) {
            result = nlSubscriberProfileStateInput.get(0).getValue();
        }
        return result;
    }
}
