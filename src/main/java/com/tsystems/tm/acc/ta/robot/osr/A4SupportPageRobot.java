package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.pages.osr.a4nemoupdater.A4SupportPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class A4SupportPageRobot {

    A4SupportPage a4SupportPage = new A4SupportPage();

    @Step("Open UI, log in, and goTo support page")
    public void openSupportPage(){
        A4SupportPage
                .login();
    }

    @Step("Click clean nemo queue button")
    public void clickCleanNemoQueueButton() {
        $(a4SupportPage.getA4_SUPPORT_UI_CLEAN_NEMO_QUEUE_BUTTON_LOCATOR()).click();
    }

    @Step("Click move from nemo dlq button")
    public void clickMoveFromDlqButton() {
        $(a4SupportPage.getA4_SUPPORT_UI_MOVE_FROM_DLQ_BUTTON_LOCATOR()).click();
    }

}
