package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileInbetriebnahmePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class A4MobileUiRobot {

    A4MobileNeSearchPage a4MobileNeSearchPage = new A4MobileNeSearchPage();
    A4MobileInbetriebnahmePage a4MobileInbetriebnahmePage = new A4MobileInbetriebnahmePage();

    @Step("Open UI, log in, and goTo Ne-mobile-search-page")
    public void openNetworkElementMobileSearchPage(){
        A4MobileNeSearchPage
                .login();
    }

    @Step("Enter vpsz")
    public void enterVpsz(String value) {
        $(a4MobileNeSearchPage.getVPSZ_INPUT_FIELD_LOCATOR()).val(value);
    }

    @Step("Read vpsz")
    public String readVpsz() {
        return $(a4MobileNeSearchPage.getVPSZ_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Click search button")
    public void clickSearchButton() {
        $(a4MobileNeSearchPage.getSEARCH_BUTTON_LOCATOR()).click();
    }

    @Step("Enter fsz")
    public void enterFsz(String value) { $(a4MobileNeSearchPage.getFSZ_INPUT_FIELD_LOCATOR()).val(value); }

    @Step("Read fsz")
    public String readFsz() {
        return $(a4MobileNeSearchPage.getFSZ_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Enter Category")
    public void enterCategory(String category) {$(a4MobileNeSearchPage.getCATEGORY_INPUT_FIELD_LOCATOR()).selectOption(category);}

    @Step("Read Category")
    public String readCategory() {
        return $(a4MobileNeSearchPage.getCATEGORY_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Check planning")
    public void checkPlanning() { $(a4MobileNeSearchPage.getPLANNING_CHECKBOX_LOCATOR()).click();}

    @Step("Check if planning is checked")
    public boolean checkIsPlanningChecked() { return $(a4MobileNeSearchPage.getPLANNING_CHECKBOX_LOCATOR()).isSelected();}

    @Step("Check operating")
    public void checkOperating() { $(a4MobileNeSearchPage.getOPERATING_CHECKBOX_LOCATOR()).click();}

    @Step("Check if operating is checked")
    public boolean checkIsOperatingChecked() { return $(a4MobileNeSearchPage.getOPERATING_CHECKBOX_LOCATOR()).isSelected();}

    @Step("Check radioButton")
    public void checkRadioButton(String index) { $(a4MobileNeSearchPage.getRADIO_BUTTON_LOCATOR()).append("[" + index + "]").click();}

    @Step("Click inbetriebnahme button")
    public void clickInbetriebnahmeButton() { $(a4MobileNeSearchPage.getINBETRIEBNAHME_BUTTON_LOCATOR()).click();}

    @Step("Back navigation")
    public void clickFinishButton() {$(a4MobileInbetriebnahmePage.getFERTIG_BUTTON_LOCATOR()).click();}

}
