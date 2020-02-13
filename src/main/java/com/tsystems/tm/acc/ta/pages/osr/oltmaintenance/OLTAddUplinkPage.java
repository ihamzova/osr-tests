package com.tsystems.tm.acc.ta.pages.osr.oltmaintenance;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;

public class OLTAddUplinkPage {
    private static final By UPLINK_OLT_ENDSZ_LOCATOR = byXpath("//input[@id='txtOltEndSZ']");
    private static final By UPLINK_OLT_SLOT_NUM_LOCATOR = byXpath("//select[@id='selectOLTSlot']");
    private static final By UPLINK_OLT_PORT_NUM_LOCATOR = byXpath("//select[@id='selectOLTPort']");
    private static final By DOWNLINK_BNG_ENDSZ_LOCATOR = byXpath("//input[@id='txtBngEndSZ']");
    private static final By DOWNLINK_BNG_SLOT_NUM_LOCATOR = byXpath("//input[@id='txtBngSlot']");
    private static final By DOWNLINK_BNG_PORT_NUM_LOCATOR = byXpath("//input[@id='txtBngPort']");
    private static final By UPLUNK_ERZEUGEN_BUTTON_LOCATOR = byXpath("//button[contains(text(),'Uplink erzeugen')]");
    private static final By LCZ_LOCATOR = byXpath("//select[@id='selectLsz']");
    private static final By ORDERNUMMER_LOCATOR = byXpath("//input[@id='txtOrdnungsnummer']");

    private static final By BACK_BUTTON_LOCATOR = byXpath("//div[@class='col-l-1']//button[@type='button']");

    private static final String OLT_ENDSZ_ATTRIBUTE_STRING = "ng-reflect-model";


    @Step("Validate page")
    public void validate() {
        $(UPLINK_OLT_ENDSZ_LOCATOR).shouldBe(visible);
    }

    public String getOLTENDSZ() {
        return $(UPLINK_OLT_ENDSZ_LOCATOR).getAttribute(OLT_ENDSZ_ATTRIBUTE_STRING);
    }

    public String[] getOLTSlotNum() {
        Select select = new Select($(UPLINK_OLT_SLOT_NUM_LOCATOR));
        List<String> options = new ArrayList<String>();
        for (WebElement elem : select.getOptions()) {
            options.add(elem.getText());
        }
        String[] result = new String[options.size()];
        return options.toArray(result);
    }

    public void setOLTSlotNum(String text) {
        Select select = new Select($(UPLINK_OLT_SLOT_NUM_LOCATOR));
        select.selectByVisibleText(text);
    }

    public void setOLTPortNum(String text) {
        Select select = new Select($(UPLINK_OLT_PORT_NUM_LOCATOR));
        select.selectByVisibleText(text);
    }

    public void setBNGENDSZ(String text) {
        $(DOWNLINK_BNG_ENDSZ_LOCATOR).val(text);
    }

    public void setBNGSlotNum(String text) {
        $(DOWNLINK_BNG_SLOT_NUM_LOCATOR).val(text);
    }

    public void setBNGPortNum(String text) {
        $(DOWNLINK_BNG_PORT_NUM_LOCATOR).val(text);
    }

    public void setLCZ(String text) {
        Select select = new Select($(LCZ_LOCATOR));
        select.selectByVisibleText(text);
    }

    public void setORDERSNUMMER(String text) {
        $(ORDERNUMMER_LOCATOR).val(text);
    }

    public void pressCreateButton() {
        $(UPLUNK_ERZEUGEN_BUTTON_LOCATOR).click();
    }

    public void pressBackButton() {
        $(BACK_BUTTON_LOCATOR).click();
    }
}
