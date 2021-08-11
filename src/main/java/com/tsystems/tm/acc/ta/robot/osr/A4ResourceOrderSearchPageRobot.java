package com.tsystems.tm.acc.ta.robot.osr;


import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceOrderSearchPage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class A4ResourceOrderSearchPageRobot {


    @Step("Open UI, log in, and goTo RO-Search-page")
    public void openRoSearchPage(){
        A4ResourceOrderSearchPage
                .login();
    }


    @Step("Enter vuep")
    public void enterRoVuep(String value) { $(A4ResourceOrderSearchPage.getRO_VUEP_NUMBER_FIELD_LOCATOR()).val(value); }

    @Step("Click search button")
    public void clickRoSearchButton() {
        $(A4ResourceOrderSearchPage.getRO_SEARCH_BUTTON_LOCATOR()).click();
    }





}
