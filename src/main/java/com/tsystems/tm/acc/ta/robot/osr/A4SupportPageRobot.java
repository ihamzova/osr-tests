package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.pages.osr.a4nemoupdater.A4SupportMovePage;
import com.tsystems.tm.acc.ta.pages.osr.a4nemoupdater.A4SupportPage;
import com.tsystems.tm.acc.ta.pages.osr.a4nemoupdater.A4SupportUnblockPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import static com.codeborne.selenide.Selenide.$;
import static org.testng.Assert.assertTrue;

@Slf4j
public class A4SupportPageRobot {

    A4SupportPage a4SupportPage = new A4SupportPage();
    A4SupportUnblockPage a4SupportUnblockPage = new A4SupportUnblockPage();
    A4SupportMovePage a4SupportMovePage = new A4SupportMovePage();

    @Step("Open UI, log in, and goTo support page")
    public void openSupportPage(){
        A4SupportPage
                .login();
    }

    @Step("Click clean nemo queue button")
    public void clickCleanNemoQueueButton() {
        $(a4SupportPage.getA4_SUPPORT_UI_CLEAN_NEMO_QUEUE_BUTTON_LOCATOR()).click();
    }

    @Step("Click clean nemo queue button - confirm")
    public void clickCleanNemoQueueButtonConfirm() {
        $(a4SupportUnblockPage.getA4_SUPPORT_UI_CLEAN_NEMO_QUEUE_BUTTON_LOCATOR()).click();
    }

    @Step("Check successful message - unblocked queue")
    public void checkCleanNemoQueueMsg() {
        assertTrue(readUnblockQueueMsg().contains("Queue is unblocked"));
    }

    @Step("")
    public String readUnblockQueueMsg(){
        return $(a4SupportUnblockPage.getA4_SUPPORT_UI_CLEAN_NEMO_QUEUE_MSG_LOCATOR()).text();
    }

    @Step("Check successful message - moved messages")
    public void checkMoveMessagesMsg() {
        assertTrue(readMoveMsg().contains("move-all-from-deadletter-queue works!"));
    }

    @Step("")
    public String readMoveMsg(){
        return $(a4SupportMovePage.getA4_SUPPORT_UI_MOVE_FROM_DLQ_MSG_LOCATOR_MSG_LOCATOR()).text();
    }

    @Step("Click move from nemo dlq button")
    public void clickMoveFromDlqButton() {
        $(a4SupportPage.getA4_SUPPORT_UI_MOVE_FROM_DLQ_BUTTON_LOCATOR()).click();
    }

}
