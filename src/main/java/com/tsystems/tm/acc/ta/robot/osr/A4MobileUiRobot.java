package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class A4MobileUiRobot {

    A4MobileNeSearchPage a4MobileNeSearchPage = new A4MobileNeSearchPage();

    @Step("Open UI, log in, and goTo Ne-mobile-search-page")
    public void openNetworkElementMobileSearchPage(){
        A4MobileNeSearchPage
                .login();
    }

    @Step("Enter vpsz")
    public void enterVpsz(String value) {
        $(a4MobileNeSearchPage.getVPSZ_INPUT_FIELD_LOCATOR()).val(value);
    }

    @Step("Click search button")
    public void clickSearchButton() {
        $(a4MobileNeSearchPage.getSEARCH_BUTTON_LOCATOR()).click();
    }

    @Step("Enter fsz")
    public void enterFsz(String value) {
        $(a4MobileNeSearchPage.getFSZ_INPUT_FIELD_LOCATOR()).val(value);
    }

}
