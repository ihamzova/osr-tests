package com.tsystems.tm.acc.ta.pages.osr.ontusage;

import com.codeborne.selenide.Condition;
import com.openshift.internal.util.Assert;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.ta.util.Screenshot;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;

@Slf4j
public class OntUsagePage implements SupplierCockpitUiPage{
    public static final String ENDPOINT = "/auftragnehmerportal-ui/ontadministration/";

    public static final By NEW_ONT_BUTTON = byXpath("//*[contains(text(),'Neuer ONT')]");
    public static final By NEW_SERIAL_INPUT = byXpath("//input[@id='serialNumber']");
    public static final By USEABLE_FOR_ALL = byXpath("//*[@id='useForAnyEmployee']");
    public static final By SUBMIT_BUTTON = byXpath("//*[contains(text(),'Anlegen')]");
    public static final By USERNAME_INPUT = byXpath("//input[@id='idmNameOfEmployee']");
    public static final By MENU_ICON = byXpath("//button[@title='Benutzermenü']");

    @Step("Validate Url")
    public OntUsagePage validate() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        $(NEW_ONT_BUTTON).isDisplayed();
        $(MENU_ICON).isDisplayed();
        return this;
    }

    @Step("Open Supplier ONT usage Page")
    public static OntUsagePage openPage(String acid) {
        URL url = new OCUrlBuilder(APP).withoutSuffix().withEndpoint(ENDPOINT).withParameter("a-cid", acid).buildExternal();
        log.info("Opening url " + url.toString());
        OntUsagePage page = open(url, OntUsagePage.class);
        return page;
    }

    @Step("Click create Button")
    public OntUsagePage clickCreateONT(){
        $(NEW_ONT_BUTTON).click();
        Screenshot.takeScreenshot();
        $(NEW_SERIAL_INPUT).waitUntil(Condition.visible, 3000);
        $(USEABLE_FOR_ALL).waitUntil(Condition.visible, 3000);
        $(SUBMIT_BUTTON).waitUntil(Condition.visible, 3000);
        return this;
    }

    @Step("Fill in ONT data and submit")
    public OntUsagePage createONT(Ont ont){
        $(NEW_SERIAL_INPUT).val(ont.getSerialNumber());
        if (ont.getAssignedEmployee() == null) {
            $(USEABLE_FOR_ALL).click();
            Screenshot.takeScreenshot();
            $(USERNAME_INPUT).waitUntil(Condition.disabled, 2000);
        } else {
            $(USERNAME_INPUT).val(ont.getAssignedEmployee());
        }
        $(SUBMIT_BUTTON).click();
        Screenshot.takeScreenshot();
        By ONT_CELL = byXpath("//cdk-cell[contains(text(),'"+ont.getSerialNumber()+"')]");
        $(ONT_CELL).waitUntil(Condition.exist,3000);
        return this;
    }

    @Step("Delete Ont for cleanup reasons")
    public OntUsagePage deleteOnt(Ont ont, boolean assertWillFail) {
        $(byXpath("//cdk-cell[contains(text(),'"+ont.getSerialNumber()+"')]/following-sibling::cdk-cell[contains(@class, 'cdk-column-trash')]")).click();
        By ONT_CELL = byXpath("//cdk-cell[contains(text(),'"+ont.getSerialNumber()+"')]");
        if (assertWillFail){
            sleep(1000); //this is needed because the error message pops up asyncronously
            By ERRORMESSAGE = byXpath("//div[contains(@class,'notification')]/*[contains(text(),'Löschen in dem Status nicht möglich!')]");
            if (!$(ERRORMESSAGE).isDisplayed()){
                throw new Assert.AssertionFailedException();
            }
            $(ONT_CELL).shouldBe(Condition.visible);
        } else {
            By CONFIRM_BUTTON = byXpath("//button[contains(@class, 'ui-confirmdialog-acceptbutton')]");
            $(CONFIRM_BUTTON).waitUntil(Condition.visible, 2000).click();
            $(ONT_CELL).waitUntil(Condition.disappears, 4000);
        }
        return this;
    }

    @Step("Logout from Supplier UI")
    public void logout(){
        $(MENU_ICON).click();
        By LOGOUT_BUTTON = byXpath("//button[@title='Ausloggen']");
        $(LOGOUT_BUTTON).waitUntil(Condition.visible, 1000).click();
        By LOGIN_BUTTON = byId("kc-login");
        $(LOGIN_BUTTON).waitUntil(Condition.visible, 2000); //check that logout was successful

    }
}
