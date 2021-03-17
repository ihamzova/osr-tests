package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceInventoryBrowserPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class A4ResourceInventoryBrowserRobot {

    @Step("Open UI, log in, and goTo search-page")
    public void openRiBrowserPage(){
        A4ResourceInventoryBrowserPage.login();     // warum wird hier die Klasse verwendet?
    }

    @Step("Click 'Inventar Suche' button")
    public void clickInventorySearchButton() {
        $(A4ResourceInventoryBrowserPage.getInventorySearchButton_Locator()).click();
    }

    @Step("Click 'Inventar Import' button")
    public void clickInventoryImportButton() {
        $(A4ResourceInventoryBrowserPage.getInventoryImportButton_Locator()).click();
    }
    @Step("Click 'Mobile UI' button")
    public void clickMobilUiButton() {
        $(A4ResourceInventoryBrowserPage.getMobilUiButton_Locator()).click();
    }

}
