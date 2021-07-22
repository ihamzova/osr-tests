package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.ta.pages.osr.a4nemoupdater.A4SupportListQueuePage;
import com.tsystems.tm.acc.ta.pages.osr.a4nemoupdater.A4SupportMovePage;
import com.tsystems.tm.acc.ta.pages.osr.a4nemoupdater.A4SupportPage;
import com.tsystems.tm.acc.ta.pages.osr.a4nemoupdater.A4SupportUnblockPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4InventarSuchePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import com.tsystems.tm.acc.tests.osr.a4.nemo.updater.client.model.QueueElement;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Slf4j
public class A4SupportPageRobot {

    A4SupportPage a4SupportPage = new A4SupportPage();
    A4SupportUnblockPage a4SupportUnblockPage = new A4SupportUnblockPage();
    A4SupportMovePage a4SupportMovePage = new A4SupportMovePage();
    A4SupportListQueuePage a4SupportListQueuePage = new A4SupportListQueuePage();

    @Step("Open UI, log in, and goTo support page")
    public void openSupportPage(){
        A4SupportPage
                .login();
    }

    @Step("Click clean nemo queue button")
    public void clickUnblockNemoQueueButton() {
        $(a4SupportPage.getA4_SUPPORT_UI_CLEAN_NEMO_QUEUE_BUTTON_LOCATOR()).click();
    }

    @Step("Click clean nemo queue button - confirm")
    public void clickMoveMessageToDlqConfirm() {
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
        assertTrue(readMoveMsg().contains("Einträge gesendet."));
    }

    @Step("")
    public String readMoveMsg(){
        return $(a4SupportMovePage.getA4_SUPPORT_UI_MOVE_FROM_DLQ_MSG_LOCATOR()).text();
    }

    @Step("Click move from nemo dlq button")
    public void clickMoveFromDlqButton() {
        $(a4SupportPage.getA4_SUPPORT_UI_MOVE_FROM_DLQ_BUTTON_LOCATOR()).click();
    }

    @Step("Click move from nemo dlq confirm button")
    public void clickMoveFromDlqConfirmButton() {
        $(a4SupportMovePage.getA4_SUPPORT_UI_MOVE_FROM_DLQ_BUTTON_LOCATOR()).click();
    }

    @Step("Click list queue button")
    public void clickListQueueButton() {
        $(a4SupportPage.getA4_SUPPORT_UI_LIST_QUEUE_BUTTON_LOCATOR()).click();
    }

    @Step("Check if table with queue list is existent")
    public void checkTable(int count) {
        int numberOfColumnsQeList = 3;
        log.info("+++ Anzahl erwarteter QueueElements in UI : "+count);     // 3 Felder pro Eintrag

        assertTrue($(a4SupportListQueuePage.getA4_SUPPORT_UI_TABLE_LOCATOR()).exists());
        // read ui
        ElementsCollection elementsCollection = getQeElementsCollection();
        log.info("+++ Anzahl QueueElements in UI : "+elementsCollection.size()/3);     // 3 Felder pro Eintrag

        // create actual result
        List<QueueElement> qeActualResultList = createQeListActualResult(elementsCollection,numberOfColumnsQeList);

        assertEquals(qeActualResultList.size(),count);

    }

    private ElementsCollection getQeElementsCollection() {
        ElementsCollection elementsCollection = $(a4SupportListQueuePage.getQueueElements_SEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));
        // waitForTableToFullyLoad(elementsCollection.size());
        return elementsCollection;
    }

    // helper 'createActualResult NE'
    private List<QueueElement> createQeListActualResult (ElementsCollection elementsCollection, int numberOfColumnsQeList){
        // create empty list
        List <QueueElement> qeActualResultList = new ArrayList<>();
        if (elementsCollection.size() > 0) {
            for (int i = 0; i < elementsCollection.size() / numberOfColumnsQeList; i++) {
                QueueElement qeActualGeneric = new QueueElement();
                qeActualResultList.add(qeActualGeneric);
            }
            //  log.info("+++ qeActualResultList: "+qeActualResultList.size());

            // read table from ui and fill list (actual result)
            for (int i = 0; i < elementsCollection.size() / numberOfColumnsQeList; i++) {
                qeActualResultList.get(i).setMessageId(elementsCollection.get(i * numberOfColumnsQeList).getText());
                qeActualResultList.get(i).setUuid(elementsCollection.get(i * numberOfColumnsQeList + 1).getText());
                qeActualResultList.get(i).setEntityType(elementsCollection.get(i * numberOfColumnsQeList + 2).getText());
                // log.info("+++ uuid: "+qeActualResultList.get(i).getUuid());
            }
        }

        // sort
/*
        qeActualResultList = qeActualResultList
                .stream().sorted(Comparator.comparing(QueueElement::getUuid))
                .collect(Collectors.toList());
*/
        return qeActualResultList;
    }



}
