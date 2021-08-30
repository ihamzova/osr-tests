package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceOrderDetailPage;

import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceOrderSearchPage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class A4ResourceOrderDetailPageRobot {


    @Step("Read vuep")
    public String readVuep() {
        return $(A4ResourceOrderDetailPage.getRO_VUEP_FIELD_LOCATOR()).val();
    }

    @Step("Read RO-ID")
    public String readRoId() {
        return $(A4ResourceOrderDetailPage.getRO_ID_FIELD_LOCATOR()).val();
    }

    @Step("Read externalOrderId")
    public String readExternalOrderId() {
        return $(A4ResourceOrderDetailPage.getRO_EXT_ORDER_ID_FIELD_LOCATOR()).val();
    }

    @Step("Read carrierBsaReferenz")
    public String readCarrierBsaReferenz() {
        return $(A4ResourceOrderDetailPage.getRO_CBR_FIELD_LOCATOR()).val();
    }

    @Step("Read rahmenvertragsnummer")
    public String readRahmenvertragsnummer() {
        return $(A4ResourceOrderDetailPage.getRO_RVNR_FIELD_LOCATOR()).val();
    }

    @Step("Read startDate")
    public String readStartDate() {
        return $(A4ResourceOrderDetailPage.getRO_START_DATE_FIELD_LOCATOR()).val();
    }

    @Step("Read completionDate")
    public String readCompletionDate() {
        return $(A4ResourceOrderDetailPage.getRO_COMPLETION_DATE_FIELD_LOCATOR()).val();
    }

    @Step("Read orderDate")
    public String readOrderDate() {
        return $(A4ResourceOrderDetailPage.getRO_ORDER_DATE_FIELD_LOCATOR()).val();
    }

    @Step("Read status")
    public String readStatus() {
        return $(A4ResourceOrderDetailPage.getRO_STATUS_FIELD_LOCATOR()).val();
    }

    public ElementsCollection getRoiElementsCollection() {
        // waitForTableToFullyLoad(elementsCollection.size());
        try {
            Thread.sleep(2000);
            return $(A4ResourceOrderDetailPage.getROI_TABLE_LOCATOR())
                    .findAll(By.xpath("tr/td"));
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }



}
