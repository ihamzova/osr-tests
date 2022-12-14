package com.tsystems.tm.acc.ta.pages.osr.accessmanagement;

import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.ta.helpers.osr.logs.TimeoutBlock;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.function.Supplier;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;
import static org.testng.Assert.assertTrue;

@Slf4j
public class AccessLinesManagementPage {

    private static final long TIMEOUT_MS = 130000;
    private static final long TIMEOUT_FOR_CHANGE = 30000;

    private static final By EDIT_BUTTON = byQaData("btn-edit");
    private static final By SAVE_AND_RECONFIGURE_BUTTON = byQaData("btn-sar");
    private static final By SAVE_LOCAL_BUTTON = byQaData("btn-save-local");
    private static final By ONT_ABMELDUNG_BUTTON = byQaData("btn-ont-dereg");
    private static final By TERMINATION_BUTTON = byQaData("btn-terminate");
    private static final By ABBRECHEN_BUTTON = byQaData("btn-cancel-edit");

    private static final By ACCESS_LINE_STATUS_DROPDOWN = byQaData("mp-status-input");

    private static final By NE_DEFAULT_PROFILE = byQaData("ne-profile-default");
    private static final By NE_DEFAULT_PROFILE_STATE = byQaData("ne-def-state-input");

    private static final By ADD_SUBSCRIBER_NE_PROFILE_BUTTON = byQaData("btn-ne-profile-add");
    private static final By NE_SUBSCRIBER_PROFILE = byQaData("ne-profile-subscriber");
    private static final By NE_SUBSCRIBER_PROFILE_STATE = byQaData("ne-sub-state-input");
    private static final By NE_SUBSCRIBER_PROFILE_ONT_STATE = byQaData("ne-sub-ontstate-input");

    private static final By NL_DEFAULT_PROFILE = byQaData("nl-profile-default");
    private static final By NL_DEFAULT_PROFILE_STATE = byQaData("nl-def-state-input");

    private static final By ADD_SUBSCRIBER_NL_PROFILE_BUTTON = byQaData("btn-nl-profile-add");
    private static final By NL_SUBSCRIBER_PROFILE = byQaData("nl-profile-subscriber");
    private static final By NL_SUBSCRIBER_PROFILE_KLSID = byQaData("nl-sub-klsid-input");
    private static final By NL_SUBSCRIBER_ACCESS_NETWORK_TYPE = byQaData("nl-sub-accessnetworktype-input");
    private static final By NL_SUBSCRIBER_PROFILE_STATE = byQaData("nl-sub-state-input");

    private static final By SERIALNUMBER_IN_NSP = byQaData("nsp-ref-serialnumber-input");
    private static final By FTTB_NE_OLT_STATE = byQaData("fttb-ne-stateolt-input");
    private static final By FTTB_NE_STATEMOSAIC = byQaData("fttb-ne-statemosaic-input");
    private static final By ONU_ACCESS_ID = byQaData("fttb-ne-onuaccessid-input");

    private static final By BESTAETIGEN_BUTTON = byQaData("am-deregistration-proceed");
    private static final By BESTAETIGEN_TERMINATION_BUTTON = byQaData("am-termination-proceed");

    private static final By LINE_ID = byXpath("//*[@class='am-primary-text']");

    private static final By NOTIFICATION = byXpath("//h2[@role = 'alert']");
    private static final By CLOSE_NOTIFICATION_BUTTON = byXpath("//*[@role='alert']/button");
    private static final By A4_CONNECTIVITY_TEST = byQaData("btn-nsp-connectivity-test");
    private static final By CONNECTIVITY_TEST = byQaData("btn-ne-connectivity-test");
    private static final By GENERATE_ANP_TAG = byQaData("btn-generate-anp-tag");
    private static final By GENERATE_ONU_ID = byQaData("btn-generate-onu-id");
    private static final By GENERATE_ONU_ACCESS_ID = byQaData("btn-generate-fttb-onu-id");
    private static final By ANP_TAG_INPUT = byQaData("mp-anptag-input");
    private static final By ONU_ID_INPUT = byQaData("mp-onu id-input");
    private static final By HOMEID_INPUT = byQaData("mp-homeid-input");

    @Step("Return on first window")
    public AccessLineSearchPage returnToAccessLinesSearchPage() {
        switchTo().window(0);
        return new AccessLineSearchPage();
    }

    @Step("Click Edit button")
    public AccessLinesManagementPage clickEditButton() {
        $(EDIT_BUTTON).click();
        return this;
    }

    @Step("Click Save and Reconfigure button")
    public AccessLinesManagementPage clickSaveAndReconfigureButton() {
        $(SAVE_AND_RECONFIGURE_BUTTON).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Der Neukonfigurationsprozess wurde gestartet"));
        closeNotificationButton();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Access Line wurde erfolgreich neu konfiguriert"));
        closeNotificationButton();
        return this;
    }

    @Step("Click Save locally button")
    public AccessLinesManagementPage clickSaveLocallyButton() {
        $(SAVE_LOCAL_BUTTON).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Access Line wurde erfolgreich aktualisiert"));
        closeNotificationButton();
        return this;
    }

    @Step("Click ONT Abmeldung Button")
    public AccessLinesManagementPage clickOntAbmeldungButton() {
        $(ONT_ABMELDUNG_BUTTON).click();
        return this;
    }

    @Step("Click Abbrechen Button")
    public AccessLinesManagementPage clickAbbrechenButton() {
        $(ABBRECHEN_BUTTON).click();
        return this;
    }

    @Step("Click termination button")
    public AccessLinesManagementPage clickTerminationButton() {
        $(TERMINATION_BUTTON).click();
        $(BESTAETIGEN_TERMINATION_BUTTON).click();
        $(NOTIFICATION).shouldHave(text("Der K??ndigungsprozess wird gestartet. Das wird einige Zeit dauern. Bitte aktualisieren Sie die Seite einige Minuten sp??ter"));
        closeNotificationButton();
        return this;
    }

    @Step("Get ONT State from NE Profile")
    public String getOntState() {
        return $(NE_SUBSCRIBER_PROFILE_ONT_STATE).getValue();
    }

    @Step("Get ONT State from NE Profile")
    public String getOnuID() {
        return $$(ONU_ID_INPUT).get(0).getValue();
    }

    @Step("Get ONT State from NE Profile")
    public String getAnpTag() {
        return $$(ANP_TAG_INPUT).get(0).getValue();
    }

    @Step("Click Best??tigen Button")
    public AccessLinesManagementPage clickBest??tigenButton() {
        $(BESTAETIGEN_BUTTON).click();
        $(NOTIFICATION).shouldHave(text("Access Line wurde erfolgreich neu konfiguriert"));
        //closeNotificationButton();
        return this;
    }

    @Step("Add Subscriber NE profile")
    public AccessLinesManagementPage addSubscriberNeProfile(String profileState, String ontState) {
        $(ADD_SUBSCRIBER_NE_PROFILE_BUTTON).click();
        $(NE_SUBSCRIBER_PROFILE_STATE).click();
        By STATE = byXpath("//li[@aria-label='" + profileState+"']");
        $(STATE).shouldBe(visible).hover().click();
        changeOntState(ontState);
        return this;
    }

    @Step("Add Subscriber NL profile")
    public AccessLinesManagementPage addSubscriberNlProfile(String klsId, String state) {
        $(ADD_SUBSCRIBER_NL_PROFILE_BUTTON).click();
        $(NL_SUBSCRIBER_PROFILE_KLSID).click();
        $(NL_SUBSCRIBER_PROFILE_KLSID).val(klsId);
        $(NL_SUBSCRIBER_PROFILE_STATE).click();
        By STATE = byXpath("//li[@aria-label='" + state+"']");
        $(STATE).click();
        return this;
    }

    @Step("Add Serialnumber to NSP")
    public AccessLinesManagementPage changeSerialNumberOnNsp(String serialnumber) {
        $(SERIALNUMBER_IN_NSP).click();
        $(HOMEID_INPUT).sendKeys((Keys.CONTROL + "a"));
        $(HOMEID_INPUT).sendKeys(Keys.DELETE);
        $(SERIALNUMBER_IN_NSP).val(serialnumber);
        return this;
    }

    @Step("Change AccessLine status")
    public AccessLinesManagementPage changeAccessLineStatus(String state) {
        $(ACCESS_LINE_STATUS_DROPDOWN).click();
        By STATE = byXpath("//li[@aria-label='" + state+"']");
        $(STATE).click();
        return this;
    }

    @Step("Change Default NE Profile State")
    public AccessLinesManagementPage changeDefaultNeProfileState(String state) {
        $(NE_DEFAULT_PROFILE_STATE).scrollIntoView(true).click();
        By STATE = byXpath("//li[@aria-label='" + state+"']");
        $(STATE).hover().shouldBe(visible).click();
        return this;
    }

    @Step("Change Default NL Profile State")
    public AccessLinesManagementPage changeDefaultNlProfileState(String state) {
        $(NL_DEFAULT_PROFILE_STATE).scrollIntoView(true).click();
        By STATE = byXpath("//li[@aria-label='" + state+"']");
        $(STATE).hover().shouldBe(visible).click();
        return this;
    }

    @Step("Change AccessNetworkType")
    public AccessLinesManagementPage changeAccessNetworkType(String accessNetworkType) {
        $(NL_SUBSCRIBER_ACCESS_NETWORK_TYPE).click();
        $(NL_SUBSCRIBER_ACCESS_NETWORK_TYPE).sendKeys((Keys.CONTROL + "a"));
        $(NL_SUBSCRIBER_ACCESS_NETWORK_TYPE).sendKeys(Keys.DELETE);
        $(NL_SUBSCRIBER_ACCESS_NETWORK_TYPE).val(accessNetworkType);
        return this;
    }

    @Step("Generate ANP_TAG")
    public AccessLinesManagementPage generateAnpTag() {
        $(GENERATE_ANP_TAG).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Ein neues anpTag wird generiert, nachdem die ??nderungen gespeichert wurden Dr??cken Sie 'Speichern/Speichern und rekonfigurieren"));
        closeNotificationButton();
        return this;
    }

    @Step("Generate ONU_ID")
    public AccessLinesManagementPage generateOnuID() {
        $(GENERATE_ONU_ID).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Eine neue ONU ID wird generiert, nachdem die ??nderungen gespeichert wurden Dr??cken Sie 'Speichern/Speichern und rekonfigurieren'"));
        closeNotificationButton();
        return this;
    }

    @Step("Generate Onu Access Id")
    public AccessLinesManagementPage generateOnuAccessId() {
        $(GENERATE_ONU_ACCESS_ID).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Eine neue Onu Access ID wird generiert, nachdem die ??nderungen gespeichert wurden Dr??cken Sie 'Speichern/Speichern und rekonfigurieren'"));
        closeNotificationButton();
        return this;
    }

    @Step("Remove HomeID")
    public AccessLinesManagementPage removeHomeID() {
        $(HOMEID_INPUT).sendKeys((Keys.CONTROL + "a"));
        $(HOMEID_INPUT).sendKeys(Keys.DELETE);
        clickSaveAndReconfigureButton();
        return this;
    }

    @Step("Start A4 connectivity test")
    public AccessLinesManagementPage startA4ConnectivityTest() {
        $(A4_CONNECTIVITY_TEST).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Der Konnektivit??tstest wird gestartet. Bitte aktualisieren Sie die Seite einige Minuten sp??ter"));
        return this;
    }

    @Step("Start connectivity test")
    public AccessLinesManagementPage startConnectivityTest() {
        $(CONNECTIVITY_TEST).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Der Konnektivit??tstest wird gestartet. Bitte aktualisieren Sie die Seite einige Minuten sp??ter"));
        return this;
    }

    @Step("Wait until needed status")
    public AccessLinesManagementPage waitUntilNeededStatus(String expectedStatus) {
        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(TIMEOUT_FOR_CHANGE); //set timeout in milliseconds
            timeoutBlock.setTimeoutInterval(1000);
            Supplier<Boolean> checkOntStatus = () -> {
                Boolean result = false;
                try {
                    refresh();
                    result = getOntState()
                            .contains(expectedStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            };
            timeoutBlock.addBlock(checkOntStatus); // execute the runnable precondition
        } catch (Throwable e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }
        return this;
    }

    @Step("Change ONT state")
    public AccessLinesManagementPage changeOntState(String state) {
        $(NE_SUBSCRIBER_PROFILE_ONT_STATE).click();
        By STATE = byXpath("//li[@aria-label='" + state+"']");
        $(STATE).shouldBe(visible).click();
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
        String result;
        // wait for sunscriber_networkline_profile template to be displayed because it always exists even if there is not sunscriber_networkline_profile itself
        $(NL_SUBSCRIBER_PROFILE).shouldBe(visible, Duration.ofMillis(TIMEOUT_MS));
        if ($(NE_DEFAULT_PROFILE).exists() && $$(NE_DEFAULT_PROFILE_STATE).size() > 0) {
            result = $(NE_DEFAULT_PROFILE_STATE).getValue();
        } else {
            result = "NULL";
        }
        return result;
    }

    @Step("Get Default NL Profile state")
    public String getNLDefaultProfileState() {
        String result;
        $(NL_SUBSCRIBER_PROFILE).shouldBe(visible, Duration.ofMillis(TIMEOUT_MS));
        if ($(NL_DEFAULT_PROFILE).exists() && $$(NL_DEFAULT_PROFILE_STATE).size() > 0) {
            result = $(NL_DEFAULT_PROFILE_STATE).getValue();
        } else {
            result = "NULL";
        }
        return result;
    }

    @Step("Get Subscriber NE Profile state")
    public String getNeSubscriberProfileState() {
        String result;
        $(NL_SUBSCRIBER_PROFILE).shouldBe(visible, Duration.ofMillis(TIMEOUT_MS));
        if ($(NE_SUBSCRIBER_PROFILE).exists() && $$(NE_SUBSCRIBER_PROFILE_STATE).size() > 0) {
            result = $(NE_SUBSCRIBER_PROFILE_STATE).getValue();
        } else {
            result = "NULL";
        }
        return result;
    }

    @Step("Get Subscriber NL Profile state")
    public String getNLSubscriberProfileState() {
        String result;
        $(NL_SUBSCRIBER_PROFILE).shouldBe(visible, Duration.ofMillis(TIMEOUT_MS));
        System.out.println($(NL_SUBSCRIBER_PROFILE).exists() && $$(NL_SUBSCRIBER_PROFILE).size() > 0);
        if ($(NL_SUBSCRIBER_PROFILE).exists() && $$(NL_SUBSCRIBER_PROFILE_STATE).size() > 0) {
            result = $(NL_SUBSCRIBER_PROFILE_STATE).getValue();
        } else {
            result = "NULL";
        }
        return result;
    }

    @Step("Get AccessNetworkType")
    public String getAccessNetworkType() {
        $(NL_SUBSCRIBER_PROFILE).shouldBe(visible, Duration.ofMillis(TIMEOUT_MS));
        return $(NL_SUBSCRIBER_ACCESS_NETWORK_TYPE).getValue();
    }

    @Step("Get Onu Access Id")
    public String getOnuAccessId() {
        $(ONU_ACCESS_ID).shouldBe(visible);
        return $(ONU_ACCESS_ID).getValue();
    }

    @Step("Get Unsynchron Tooltip")
    public SelenideElement getUnsynchronTooltip(String profileNameQaData) {
        By UNSYNCHRON = byXpath("//*[@qa-data='" + profileNameQaData+"']//*[contains (@class, 'am-profile-title__outofsync')]");
        return $(UNSYNCHRON);
    }

    @Step("Get Olt State from NE Profile")
    public String getOltStatefromNeProfile() {
        return $$(FTTB_NE_OLT_STATE).get(0).getValue();
    }

    @Step("Get Mosaic State")
    public String getMosaicStatefromNeProfile() {
        return $$(FTTB_NE_STATEMOSAIC).get(0).getValue();
    }

    @Step("Check AccessLine profiles states")
    public void checkAccessLineProfilesStates(String neExpectedDefaultProfileState,
                                              String neExpectedSubscriberProfileState, String nlExpectedDefaultProfileState,
                                              String nlExpectedSubscriberProfileState) {
        assertTrue(getNeDefaultProfileState().contains(neExpectedDefaultProfileState), "DefaultNeProfile State is incorrect");
        assertTrue(getNeSubscriberProfileState().contains(neExpectedSubscriberProfileState), "SubscriberNeProfile State is incorrect");
        assertTrue(getNLDefaultProfileState().contains(nlExpectedDefaultProfileState), "DefaultNetworkLineProfile State is incorrect");
        assertTrue(getNLSubscriberProfileState().contains(nlExpectedSubscriberProfileState), "SubscriberNetworkLineProfile State is incorrect");
    }

    @Step("Check FTTB AccessLine profiles states")
    public void checkFTTBProfiles(String nlExpectedDefaultProfileState,
                                  String nlExpectedSubscriberProfileState, String oltExpectedState, String mosaicExpectedState) {
        assertTrue(getOltStatefromNeProfile().contains(oltExpectedState));
        assertTrue(getMosaicStatefromNeProfile().contains(mosaicExpectedState));
        assertTrue(getNLDefaultProfileState().contains(nlExpectedDefaultProfileState));
        assertTrue(getNLSubscriberProfileState().contains(nlExpectedSubscriberProfileState));
    }

    @Step("Close Notification button")
    public AccessLinesManagementPage closeNotificationButton() {
        $(CLOSE_NOTIFICATION_BUTTON).click();
        return this;
    }
}
