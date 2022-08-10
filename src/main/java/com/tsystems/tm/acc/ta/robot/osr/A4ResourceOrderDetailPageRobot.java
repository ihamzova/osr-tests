package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceOrderDetailPage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class A4ResourceOrderDetailPageRobot {


    @Step("Read vuep")
    public String readVuep() {
        SelenideElement container = $(A4ResourceOrderDetailPage.getCONTAINER_MAIN_DATA_A10NSP_RO());

        return $(container.findElement(A4ResourceOrderDetailPage.getRO_PUBLIC_REFERENCE_ID_FIELD_LOCATOR())).innerHtml();

    }

    @Step("Read RO-ID")
    public String readRoId() {
        SelenideElement container = $(A4ResourceOrderDetailPage.getCONTAINER_MAIN_DATA_A10NSP_RO());

        return $(container.findElement(A4ResourceOrderDetailPage.getRO_ID_FIELD_LOCATOR())).innerHtml();
    }

    @Step("Read externalOrderId")
    public String readExternalOrderId() {
        SelenideElement container = $(A4ResourceOrderDetailPage.getCONTAINER_MAIN_DATA_A10NSP_RO());

        return $(container.findElement(A4ResourceOrderDetailPage.getRO_EXT_ORDER_ID_FIELD_LOCATOR())).innerHtml();


    }

    @Step("Read carrierBsaReference")
    public String readCarrierBsaReference() {
        SelenideElement container = $(A4ResourceOrderDetailPage.getCONTAINER_MAIN_DATA_A10NSP_RO());

        return $(container.findElement(A4ResourceOrderDetailPage.getRO_CBR_FIELD_LOCATOR())).innerHtml();
    }

    @Step("Read frameContractId")
    public String readFrameContractId() {
        SelenideElement container = $(A4ResourceOrderDetailPage.getCONTAINER_MAIN_DATA_A10NSP_RO());

        return $(container.findElement(A4ResourceOrderDetailPage.getRO_FRAME_CONTRACT_ID_FIELD_LOCATOR())).innerHtml();
    }

    @Step("Read startDate")
    public String readStartDate() {
        SelenideElement container = $(A4ResourceOrderDetailPage.getCONTAINER_MAIN_DATA_A10NSP_RO());

        return $(container.findElement(A4ResourceOrderDetailPage.getRO_START_DATE_FIELD_LOCATOR())).innerHtml();
    }

    @Step("Read completionDate")
    public String readCompletionDate() {
        SelenideElement container = $(A4ResourceOrderDetailPage.getCONTAINER_MAIN_DATA_A10NSP_RO());

        return $(container.findElement(A4ResourceOrderDetailPage.getRO_COMPLETION_DATE_FIELD_LOCATOR())).innerHtml();

    }

    @Step("Read orderDate")
    public String readOrderDate() {
        SelenideElement container = $(A4ResourceOrderDetailPage.getCONTAINER_MAIN_DATA_A10NSP_RO());

        return $(container.findElement(A4ResourceOrderDetailPage.getRO_ORDER_DATE_FIELD_LOCATOR())).innerHtml();

    }




    @Step("Read status")
    public String readStatus() {
        SelenideElement container = $(A4ResourceOrderDetailPage.getCONTAINER_MAIN_DATA_A10NSP_RO());


        return $(container.findElement(A4ResourceOrderDetailPage.getRO_STATUS_FIELD_LOCATOR())).innerHtml();
    }

    public ElementsCollection getRoiElementsCollection() {
        // waitForTableToFullyLoad(elementsCollection.size());
        try {
            Thread.sleep(2000);
            SelenideElement table = $(A4ResourceOrderDetailPage.getROI_TABLE_LOCATOR());
            return table
                    .findAll(By.xpath(".//tr[starts-with(@id,'trtblA10nspRoItems')]/td"));

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }



}
