package com.tsystems.tm.acc.ta.pages.osr.accessmanagement;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.util.Set;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class AccessLinesManagementPage {

    private static final String TITLE = "Access Lines durchsuchen";
    private static final long TIMEOUT_MS = 30000;

    private static final By NE_DEFAULT_PROFILE_STATE_INPUT = byQaData("ne-def-state-input");
    private static final By NL_DEFAULT_PROFILE_STATE_INPUT = byQaData("nl-def-state-input");
    private static final By NE_SUBSCRIBER_PROFILE_STATE_INPUT = byQaData("ne-sub-state-input");
    private static final By NL_SUBSCRIBER_PROFILE_STATE_INPUT = byQaData("nl-sub-state-input");

    SelenideElement neDefaultProfileTitle = $x("//am-al-ne-profile//*[contains(text(), 'Default Profile')]");
    SelenideElement neSubscriberProfileTitle = $x("//am-al-ne-profile//*[contains(text(), 'Subscriber Profile')]");
    SelenideElement nlDefaultProfileTitle = $x("//am-al-nl-profile//*[contains(text(), 'Default Profile')]");
    SelenideElement nlSubscriberProfileTitle = $x("//am-al-nl-profile//*[contains(text(), 'Subscriber Profile')]");
    SelenideElement editButton = $x("//*[@class='am-form-btn am-primary am-text-center am-pointer p-ripple ng-star-inserted']");
    SelenideElement saveAndReconfigureButton = $x("//*[@class='am-form-btn am-primary am-text-center am-pointer p-ripple ng-star-inserted']");
    SelenideElement ontAbmeldungButton = $x("//*[@class='am-form-btn am-primary am-text-center am-pointer p-ripple ng-star-inserted']");

    SelenideElement addProfile = $x("//*[@class='am-icon icon--add-profile']");
    SelenideElement dropDownTriggerStateNEprofile = $x("//*[@class='p-dropdown-trigger-icon ng-tns-c49-1 pi pi-chevron-down']");
    SelenideElement dropDownTriggerStateSubProfile = $x("//*[@class='p-dropdown-trigger-icon ng-tns-c49-4 pi pi-chevron-down']");
    SelenideElement dropDownTriggerOntState = $x("//*[@class='p-dropdown-trigger-icon ng-tns-c49-5 pi pi-chevron-down']");
    SelenideElement dropDownTriggerStatus = $x("//*[@class='p-dropdown-trigger-icon ng-tns-c49-3 pi pi-chevron-down']");//
    SelenideElement dropDownTriggerStatusToWG = $x("//*[@class='p-dropdown-trigger-icon ng-tns-c49-10 pi pi-chevron-down']");

    SelenideElement inactiveState = $x("//*[@id='INACTIVE']");
    SelenideElement activeState = $x("//*[@id='ACTIVE']");
    SelenideElement onlineOntState = $x("//*[@id='ONLINE']");
    SelenideElement assignedStatus = $x("//*[@id='ASSIGNED']");
    SelenideElement walledGardenStatus = $x("//*[@id='WALLED_GARDEN']");
    SelenideElement bestatigenData = $x("//*[@qa-data='am-deregistration-proceed']");


    ElementsCollection neDefaultProfileStateInput = $$(NE_DEFAULT_PROFILE_STATE_INPUT);
    ElementsCollection nlDefaultProfileStateInput = $$(NL_DEFAULT_PROFILE_STATE_INPUT);
    ElementsCollection neSubscriberProfileStateInput = $$(NE_SUBSCRIBER_PROFILE_STATE_INPUT);
    ElementsCollection nlSubscriberProfileStateInput = $$(NL_SUBSCRIBER_PROFILE_STATE_INPUT);


    @Step("Return on first window")
    public void returnOnWindowWithListOfLines(){
        Selenide.switchTo().window(0);
        Selenide.refresh();
    }

    @Step("Click edit button")
    public void clickEditButton(){
        editButton.click();
    }

    @Step("Click ONT Abmeldung Button")
    public void clickONTAbmeldungButton(){
        ontAbmeldungButton.click();
    }

    @Step("Click Bestätigen Button")
    public void clickBestätigenButton() throws InterruptedException {
        bestatigenData.click();
        Thread.sleep(5000);


    }

    @Step("Click save and reconfigure button")
    public void clickSaveAndReconfigureButton(){
        saveAndReconfigureButton.click();
    }

    @Step("Add subscriber profile")
    public void  addSubscriberProfile()  {
        addProfile.click();
        dropDownTriggerStateSubProfile.click();
        activeState.click();
        dropDownTriggerOntState.click();
        onlineOntState.click();
        dropDownTriggerStatus.click();
        assignedStatus.click();

    }

    public  void changeStatusOnWalledGarden(){
        dropDownTriggerStatusToWG.click();
        walledGardenStatus.click();
    }

    @Step("Change default profile")
    public void  changeDefaultProfile()  {
        dropDownTriggerStateNEprofile.click();
        inactiveState.click();
    }

    public String getPageTitle() {
        return TITLE;
    }

    @Step("Get NE default profile state")
    public String getNEDefaultProfileState() {
        String result = "NULL";
        neDefaultProfileTitle.waitUntil(Condition.visible, TIMEOUT_MS);
        if (neDefaultProfileStateInput.size() > 0) {
            result = neDefaultProfileStateInput.get(0).getValue();
        }
        return result;
    }

    @Step("Get NL default profile state")
    public String getNLDefaultProfileState() {
        String result = "NULL";
        nlDefaultProfileTitle.waitUntil(Condition.visible, TIMEOUT_MS);
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
        nlSubscriberProfileTitle.waitUntil(Condition.visible, TIMEOUT_MS);
        if (nlSubscriberProfileStateInput.size() > 0) {
            result = nlSubscriberProfileStateInput.get(0).getValue();
        }
        return result;
    }
}
