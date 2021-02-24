package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4InventarSuchePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileInbetriebnahmePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileMonitoringPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Slf4j
public class A4InventarSucheRobot {


    // helper method 'wait'
    public void waitForTableToFullyLoad(int numberOfElements){
        $(By.xpath("//tr[" + numberOfElements + "]")).shouldBe(Condition.visible);
    }

    A4InventarSuchePage a4InventarSuchePage = new A4InventarSuchePage();

    public ElementsCollection getElementsCollection () {
        ElementsCollection elementsCollection = $(a4InventarSuchePage.getSEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));
        // waitForTableToFullyLoad(elementsCollection.size());
        return elementsCollection;
    }


    @Step("Checkbox WORKING")
    public void checkboxWorking() { $(a4InventarSuchePage.getWORKING_CHECKBOX_LOCATOR()).click();}

    @Step("Checkbox Op INSTALLING")
    public void checkboxOpInstalling() { $(a4InventarSuchePage.getOPS_INSTALLING_CHECKBOX_LOCATOR()).click();}

    @Step("Checkbox NOT WORKING")
    public void checkboxNotWorking() { $(a4InventarSuchePage.getNOT_WORKING_CHECKBOX_LOCATOR()).click();}

    @Step("Checkbox NOT MANAGEABLE")
    public void checkboxNotManageable() { $(a4InventarSuchePage.getNOT_MANAGEABLE_CHECKBOX_LOCATOR()).click();}

    @Step("Checkbox FAILED")
    public void checkboxFailed() { $(a4InventarSuchePage.getFAILED_CHECKBOX_LOCATOR()).click();}

    @Step("Checkbox ACTIVATING")
    public void checkboxActivating() { $(a4InventarSuchePage.getACTIVATING_CHECKBOX_LOCATOR()).click();}

    @Step("Checkbox DEACTIVATING")
    public void checkboxDeactivating() { $(a4InventarSuchePage.getDEACTIVATING_CHECKBOX_LOCATOR()).click();}

    @Step("Checkbox PLANNING")
    public void checkboxPlanning() { $(a4InventarSuchePage.getPLANNING_CHECKBOX_LOCATOR()).click();}

    @Step("Checkbox Life INSTALLING")
    public void checkboxLifeInstalling() { $(a4InventarSuchePage.getLIFECYCLE_INSTALLING_CHECKBOX_LOCATOR()).click();}

    @Step("Checkbox OPERATING")
    public void checkboxOperating() { $(a4InventarSuchePage.getOPERATING_CHECKBOX_LOCATOR()).click();}

    @Step("Checkbox RETIRING")
    public void checkboxRetiring() { $(a4InventarSuchePage.getRETIRING_CHECKBOX_LOCATOR()).click();}

    @Step("Open UI, log in, and goTo Inventar-Suche-page")
    public void openInventarSuchePage(){
        A4InventarSuchePage
                .login();
    }

    @Step("Choose search by NetworkElementGroupName")
    public void clickNetworkElementGroup() {
        $(a4InventarSuchePage.getNEG_CHOOSE_BUTTON_LOCATOR()).click();
    }

    @Step("Enter neg name")
    public void enterNegName(String value) {
        $(a4InventarSuchePage.getNEG_NAME_INPUT_FIELD_LOCATOR()).val(value);
    }

    @Step("Read neg name")
    public String readNegName() {
        return $(a4InventarSuchePage.getNEG_NAME_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Click search button")
    public void clickSearchButton() {
        $(a4InventarSuchePage.getSEARCH_BUTTON_LOCATOR()).click();
    }

}
