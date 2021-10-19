package com.tsystems.tm.acc.ta.pages.osr.accessmanagement;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;
import static org.testng.Assert.assertTrue;

@Slf4j
public class AccessLinesManagementPage {

    private static final long TIMEOUT_MS = 30000;

    private static final By EDIT_BUTTON = byQaData("btn-edit");
    private static final By SAVE_AND_RECONFIGURE_BUTTON = byQaData("btn-sar");
    private static final By ONT_ABMELDUNG_BUTTON = byQaData("btn-ont-dereg");
    private static final By ADD_SUBSCRIBER_NE_PROFILE_BUTTON = byQaData("btn-ne-profile-add");
    private static final By NE_DEFAULT_PROFILE_STATE_INPUT = byQaData("ne-def-state-input");
    private static final By NL_DEFAULT_PROFILE_STATE_INPUT = byQaData("nl-def-state-input");
    private static final By NE_SUBSCRIBER_PROFILE_STATE_INPUT = byQaData("ne-sub-state-input");
    private static final By NL_SUBSCRIBER_PROFILE_STATE_INPUT = byQaData("nl-sub-state-input");
    private static final By NE_SUBSCRIBER_ONT_STATE_INPUT = byQaData("ne-sub-ontstate-input");
    private static final By ACCESS_LINE_STATUS_DROPDOWN = byQaData("mp-status-input");
    private static final By BESTAETIGEN_BUTTON = byQaData("am-deregistration-proceed");

    private static final By LINE_ID = byXpath("//*[@class='am-primary-text']");
    private static final By NE_DEFAULT_PROFILE_TITLE = byXpath("//am-al-ne-profile//*[contains(text(), 'Default Profile')]");
    private static final By NE_SUBSCRIBER_PROFILE_TITLE = byXpath("//am-al-ne-profile//*[contains(text(), 'Subscriber Profile')]");
    private static final By NL_DEFAULT_PROFILE_TITLE = byXpath("//am-al-nl-profile//*[contains(text(), 'Default Profile')]");
    private static final By NL_SUBSCRIBER_PROFILE_TITLE = byXpath("//am-al-nl-profile//*[contains(text(), 'Subscriber Profile')]");

    private static final By INACTIVE_STATE = byXpath("//*[@id='INACTIVE']");
    private static final By ACTIVE_STATE = byXpath("//*[@id='ACTIVE']");
    private static final By ONLINE_ONT_STATE = byXpath("//*[@id='ONLINE']");
    private static final By ASSIGNED_STATUS = byXpath("//*[@id='ASSIGNED']");
    private static final By WALLED_GARDEN_STATUS = byXpath("//*[@id='WALLED_GARDEN']");
    private static final By NOTIFICATION = byXpath("//h2[@role = 'alert']");
    private static final By CLOSE_NOTIFICATION_BUTTON =byXpath("//*[@role='alert']/button");

    @Step("Return on first window")
    public AccessLineSearchPage returnToAccessLinesSearchPage(){
        switchTo().window(0);
        return new AccessLineSearchPage();
    }

    @Step("Click Edit button")
    public AccessLinesManagementPage clickEditButton(){
        $(EDIT_BUTTON).click();
        return this;
    }

    @Step("Click Save and Reconfigure button")
    public AccessLinesManagementPage clickSaveAndReconfigureButton(){
        $(SAVE_AND_RECONFIGURE_BUTTON).click();
        $(NOTIFICATION).shouldHave(text("Der Neukonfigurationsprozess wurde gestartet"));
        closeNotificationButton();
        return this;
    }

    @Step("Click ONT Abmeldung Button")
    public AccessLinesManagementPage clickOntAbmeldungButton(){
        $(ONT_ABMELDUNG_BUTTON).click();
        return this;
    }

    @Step("Click Bestätigen Button")
    public AccessLinesManagementPage clickBestätigenButton()  {
        $(BESTAETIGEN_BUTTON).click();
        $(NOTIFICATION).shouldHave(text("Access Line wurde erfolgreich neu konfiguriert"));
        closeNotificationButton();
        return this;
    }

    @Step("Add Subscriber NE profile")
    public AccessLinesManagementPage addSubscriberNeProfile()  {
        $(ADD_SUBSCRIBER_NE_PROFILE_BUTTON).click();
        $(NE_SUBSCRIBER_PROFILE_STATE_INPUT).click();
        $(ACTIVE_STATE).click();
        $(NE_SUBSCRIBER_ONT_STATE_INPUT).click();
        $(ONLINE_ONT_STATE).click();
        $(ACCESS_LINE_STATUS_DROPDOWN).click();
        $(ASSIGNED_STATUS).click();
        return this;
    }

    @Step("Change AccessLine status to WALLED_GARDEN")
    public AccessLinesManagementPage changeAccessLineStatusToWalledGarden(){
        $(ACCESS_LINE_STATUS_DROPDOWN).click();
        $(WALLED_GARDEN_STATUS).click();
        return this;
    }

    @Step("Change Default NE Profile State to Inactive")
    public AccessLinesManagementPage changeDefaultProfileStateToInactive()  {
        $(NE_DEFAULT_PROFILE_STATE_INPUT).click();
        $(INACTIVE_STATE).click();
        return this;
    }

    @Step("Close AccessLine Management Page")
    public void closeCurrentTab() {
        switchTo().window(1).close();
    }

    @Step("Get LineID")
    public String getLineId() {
        return $(LINE_ID).getText();
    }

    @Step("Get Default NE Profile state")
    public String getNeDefaultProfileState() {
        String result = "NULL";
        $(NE_DEFAULT_PROFILE_TITLE).waitUntil(Condition.visible, TIMEOUT_MS);
        if ($$(NE_DEFAULT_PROFILE_STATE_INPUT).size() > 0) {
            result = $$(NE_DEFAULT_PROFILE_STATE_INPUT).get(0).getValue();
        }
        return result;
    }

    @Step("Get Default NL Profile state")
    public String getNLDefaultProfileState() {
        String result = "NULL";
        $(NL_DEFAULT_PROFILE_TITLE).waitUntil(Condition.visible, TIMEOUT_MS);
        if ($$(NL_DEFAULT_PROFILE_STATE_INPUT).size() > 0) {
            result = $$(NL_DEFAULT_PROFILE_STATE_INPUT).get(0).getValue();
        }
        return result;
    }

    @Step("Get Subscriber NE Profile state")
    public String getNeSubscriberProfileState() {
        String result = "NULL";
        $(NE_SUBSCRIBER_PROFILE_TITLE).waitUntil(Condition.visible, TIMEOUT_MS);
        if ($$(NE_SUBSCRIBER_PROFILE_STATE_INPUT).size() > 0) {
            result = $$(NE_SUBSCRIBER_PROFILE_STATE_INPUT).get(0).getValue();
        }
        return result;
    }

    @Step("Get Subscriber NL Profile state")
    public String getNLSubscriberProfileState() {
        String result = "NULL";
        $(NL_SUBSCRIBER_PROFILE_TITLE).waitUntil(Condition.visible, TIMEOUT_MS);
        if ($$(NL_SUBSCRIBER_PROFILE_STATE_INPUT).size() > 0) {
            result = $$(NL_SUBSCRIBER_PROFILE_STATE_INPUT).get(0).getValue();
        }
        return result;
    }

    @Step("Check AccessLine profiles states")
    public void checkAccessLineProfilesStates(String neExpectedDefaultProfileState,
                                              String neExpectedSubscriberProfileState, String nlExpectedDefaultProfileState,
                                              String nlExpectedSubscriberProfileState) {
        assertTrue(getNeDefaultProfileState().contains(neExpectedDefaultProfileState));
        assertTrue(getNeSubscriberProfileState().contains(neExpectedSubscriberProfileState));
        assertTrue(getNLDefaultProfileState().contains(nlExpectedDefaultProfileState));
        assertTrue(getNLSubscriberProfileState().contains(nlExpectedSubscriberProfileState));
    }
    @Step ("Close Notification button")
    public void closeNotificationButton (){
        $(CLOSE_NOTIFICATION_BUTTON).click();
    }
}
