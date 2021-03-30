package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4InventarSuchePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceInventoryBrowserPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceInventoryNeDetailPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class A4ResourceInventoryNeDetailRobot {

    @Step("Read uuid ne")
    public String readNeUuid() {
        return $(A4ResourceInventoryNeDetailPage.getNE_UUID_FIELD_LOCATOR()).val();
    }

    @Step("Read vpsz ne")
    public String readNeVpsz() {
        return $(A4ResourceInventoryNeDetailPage.getNE_VPSZ_FIELD_LOCATOR()).val();
    }

    @Step("Read fsz ne")
    public String readNeFsz() {
        return $(A4ResourceInventoryNeDetailPage.getNE_FSZ_FIELD_LOCATOR()).val();
    }

    @Step("Read category ne")
    public String readNeCategory() {
        return $(A4ResourceInventoryNeDetailPage.getNE_CATEGORY_FIELD_LOCATOR()).val();
    }

    @Step("Read type ne")
    public String readNeType() {
        return $(A4ResourceInventoryNeDetailPage.getNE_TYPE_FIELD_LOCATOR()).val();
    }

    @Step("Read PD Name ne")
    public String readNePlanningDeviceName() {
        return $(A4ResourceInventoryNeDetailPage.getNE_PlanningDeviceName_FIELD_LOCATOR()).val();
    }

    @Step("Read kls-id ne")
    public String readNeKlsId() {
        return $(A4ResourceInventoryNeDetailPage.getNE_KLSID_FIELD_LOCATOR()).val();
    }

    @Step("Read ztp ident ne")
    public String readNeZtpid() {
        return $(A4ResourceInventoryNeDetailPage.getNE_ZTPID_FIELD_LOCATOR()).val();
    }

    @Step("Read ops ne")
    public String readNeOps() {
        return $(A4ResourceInventoryNeDetailPage.getNE_OPS_FIELD_LOCATOR()).val();
    }

    @Step("Read lcs ne")
    public String readNeLcs() {
        return $(A4ResourceInventoryNeDetailPage.getNE_LCS_FIELD_LOCATOR()).val();
    }

    @Step("Read creation time ne")
    public String readNeCreationTime() {
        return $(A4ResourceInventoryNeDetailPage.getNE_CreationTime_FIELD_LOCATOR()).val();
    }

    @Step("Read last update time ne")
    public String readNeLastUpdateTime() {
        return $(A4ResourceInventoryNeDetailPage.getNE_LastUpdateTime_FIELD_LOCATOR()).val();
    }




    @Step("Open UI, log in")
    public void openRiNeDetailPage(){
        A4ResourceInventoryNeDetailPage.login();     // warum wird hier die Klasse verwendet?
    }

    public ElementsCollection getNelElementsCollection() {
        ElementsCollection elementsCollection = $(A4ResourceInventoryNeDetailPage.getNEL_SEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));
        // waitForTableToFullyLoad(elementsCollection.size());
        return elementsCollection;
    }



}
