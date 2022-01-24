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
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
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
    public static final By CONFIRM_BUTTON = byXpath("//span[contains(text(), 'Ja')]");
    public static final By ERRORMESSAGE = byXpath("//div[contains(@class,'notification')]/*[contains(text(),'Löschen in dem Status nicht möglich!')]");
    public static final By LOGIN_BUTTON = byId("kc-login");
    public static final By LOGOUT_BUTTON = byXpath("//button[@title='Ausloggen']");

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
        $(NEW_SERIAL_INPUT).shouldBe(visible, Duration.ofMillis(3000));
        $(USEABLE_FOR_ALL).shouldBe(visible, Duration.ofMillis(3000));
        $(SUBMIT_BUTTON).shouldBe(visible, Duration.ofMillis(3000));
        return this;
    }

    @Step("Fill in ONT data and submit")
    public OntUsagePage createONT(Ont ont){
        $(NEW_SERIAL_INPUT).val(ont.getSerialNumber());
        if (ont.getAssignedEmployee() == null) {
            $(USEABLE_FOR_ALL).click();
            Screenshot.takeScreenshot();
            $(USERNAME_INPUT).shouldBe(disabled, Duration.ofMillis(2000));
        } else {
            $(USERNAME_INPUT).val(ont.getAssignedEmployee());
        }
        $(SUBMIT_BUTTON).click();
        Screenshot.takeScreenshot();
        By ONT_CELL = byXpath("//cdk-cell[contains(text(),'"+ont.getSerialNumber()+"')]");
        $(ONT_CELL).should(exist,Duration.ofMillis(3000));
        return this;
    }

    @Step("Delete Ont for cleanup reasons")
    public OntUsagePage deleteOnt(Ont ont, boolean assertWillFail) {
        $(byXpath("//cdk-cell[contains(text(),'"+ont.getSerialNumber()+"')]/following-sibling::cdk-cell[contains(@class, 'cdk-column-trash')]")).click();
        By ONT_CELL = byXpath("//cdk-cell[contains(text(),'"+ont.getSerialNumber()+"')]");
        if (assertWillFail){
            sleep(1000); //this is needed because the error message pops up asyncronously
            if (!$(ERRORMESSAGE).isDisplayed()){
                throw new Assert.AssertionFailedException();
            }
            $(ONT_CELL).shouldBe(visible);
        } else {
            $(CONFIRM_BUTTON).shouldBe(visible, Duration.ofMillis(2000)).click();
            $(ONT_CELL).should(disappear, Duration.ofMillis(4000));
        }
        return this;
    }

    @Step("Logout from Supplier UI")
    public void logout(){
        $(MENU_ICON).click();
        $(LOGOUT_BUTTON).shouldBe(visible, Duration.ofMillis(1000)).click();
        $(LOGIN_BUTTON).shouldBe(visible, Duration.ofMillis(2000)); //check that logout was successful

    }
}
