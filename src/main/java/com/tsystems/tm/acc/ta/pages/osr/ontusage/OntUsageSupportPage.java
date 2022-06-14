package com.tsystems.tm.acc.ta.pages.osr.ontusage;

import com.codeborne.selenide.Condition;
import com.openshift.internal.util.Assert;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.data.osr.models.Supplier;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.URL;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class OntUsageSupportPage {
    public static final String APP = "ont-usage-support-ui-app";

    public static final By SUPPLIER_SELECTION = byXpath("//*[contains(@data-qa,'suppliers-select')]");
    public static final By SUPPLIER_SEARCH = byXpath("//input[contains(@role, 'textbox')]");
    public static final By MENU_ICON = byXpath("//button[contains(@title,'header.user-menu-title')]");
    public static final By ONT_SEARCH_FIELD = byXpath("//*[contains(@data-qa, 'search-input')]");
    public static final By DETAIL_STATE = byXpath("//div[contains(text(),'Status')]/following-sibling::div[contains(@class,'field-value')]");
    public static final By DETAIL_SUPPLIER = byXpath("//div[contains(text(),'Giga Vertragspartner')]/following-sibling::div[contains(@class,'field-value')]");
    public static final By PERMISSION_DENIED_MSG = byXpath("//div[contains(@class,'permission-denied-message')]");
    public static final By LOGOUT_BUTTON = byXpath("//button[@title='Ausloggen']");
    public static final By LOGIN_BUTTON = byId("kc-login");
    public static final By ERRORMESSAGE = byXpath("//div[contains(@role,'alert')]");
    public static final By CONFIRM_BUTTON = byXpath("//span[contains(text(), 'Ja')]");

    @Step("Validate Url")
    public OntUsageSupportPage validate() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        $(MENU_ICON).shouldBe(visible, Duration.ofMillis(3000));
        return this;
    }

    @Step("Open Supplier ONT usage Page")
    public static OntUsageSupportPage openPage() {
        URL url = new OCUrlBuilder(APP).withoutSuffix().buildExternal();
        log.info("Opening url " + url.toString());
        OntUsageSupportPage page = open(url, OntUsageSupportPage.class);
        return page;
    }

    @Step("Select supplier for search")
    public OntUsageSupportPage selectSupplier(Supplier supplier) {
        $(SUPPLIER_SELECTION).click();
        $(SUPPLIER_SEARCH).shouldBe(visible, Duration.ofMillis(2000));
        By SUPPLIER_LABEL = byXpath("//li[@aria-label='"+supplier.getSupplierName()+"']");
        $(SUPPLIER_LABEL).shouldBe(visible, Duration.ofMillis(3000));
        $(SUPPLIER_SEARCH).val(supplier.getSupplierName());
        $(SUPPLIER_LABEL).shouldBe(visible, Duration.ofMillis(3000)).click();
        $(SUPPLIER_SELECTION).click(); //close supplier selection
        return this;
    }

    @Step("update ONT to given status")
    public OntUsageSupportPage updateStatusOfOnt(Ont ont, String status) {
        By ONT_CELL = byXpath("//td[@class='serial-cell']/a[contains(text(),'"+ont.getSerialNumber()+"')]");
        $(ONT_CELL).should(exist,Duration.ofMillis(3000)).scrollTo().shouldBe(visible, Duration.ofMillis(3000));
        By STATUS_DROPDOWN = byXpath("//td[@class='serial-cell']/a[contains(text(),'"+ont.getSerialNumber()+"')]/parent::td/following-sibling::td[2]/p-dropdown");
        $(STATUS_DROPDOWN).shouldBe(visible, Duration.ofMillis(1000)).click();
        WebElement desiredStatusField = $(STATUS_DROPDOWN).findElement(byXpath("//li[contains(@aria-label,'"+status+"')]"));
        $(desiredStatusField).shouldBe(visible, Duration.ofMillis(1000)).click();
        sleep(1000); //this one is needed because the usability of errors is not so nice.. it might popup..
        if ($(ERRORMESSAGE).isDisplayed()){ //this is also needed because of usability the error pops up pretty short..
            throw new Assert.AssertionFailedException();
        }
        return this;
    }

    @Step("delete ONT via support ui")
    public OntUsageSupportPage deleteOnt(Ont ont){
        By DELETE_ONT_BUTTON = byXpath("//td/a[contains(text(),'"+ont.getSerialNumber()+"')]/parent::td/following-sibling::td[contains(@class, 'button-cell')]/i");
        $(DELETE_ONT_BUTTON).shouldBe(Condition.visible).click();
        $(CONFIRM_BUTTON).shouldBe(visible, Duration.ofMillis(1000)).click();
        By ONT_FIELD = byXpath("//td/a[contains(text(),'"+ont.getSerialNumber() + "')]");
        $(ONT_FIELD).should(disappear, Duration.ofMillis(2000));
        return this;
    }

    @Step("Filter by ONT")
    public OntUsageSupportPage filterBySerialNumber(Ont ont){
        $(ONT_SEARCH_FIELD).val(ont.getSerialNumber());
        return this;
    }

    @Step("delete workorder via support ui")
    public OntUsageSupportPage deleteWorkOrder(Ont ont){
        By DELETE_ONT_BUTTON = byXpath(String.format("//*[@data-qa-delete-workorderid='%s']", ont.getSerialNumber()));
        $(DELETE_ONT_BUTTON).shouldBe(visible,Duration.ofMillis(1000)).click();
        By CONFIRMATION_DIALOG = byClassName("p-dialog-header");
        $(CONFIRMATION_DIALOG).shouldBe(visible);
        By CONFIRMATION_BUTTON = byText("Ja");
        $(CONFIRMATION_BUTTON).click();
        $(DELETE_ONT_BUTTON).shouldNotBe(visible, Duration.ofMillis(2000));
        return this;
    }

    @Step("change Supplier")
    public OntUsageSupportPage changeSupplier(Ont ont, Supplier newSupplier){
        By ONT_CELL = byXpath("//td[@class='serial-cell']/a[contains(text(),'"+ont.getSerialNumber()+"')]");
        $(ONT_CELL).should(exist,Duration.ofMillis(3000)).scrollTo().shouldBe(visible, Duration.ofMillis(3000));
        By SUPPLIER_SELECTION = byXpath("//td/a[contains(text(),'"+ont.getSerialNumber()+"')]/parent::td/following-sibling::td[contains(@class, 'dropdown-cell')][1]//p-dropdown");
        $(SUPPLIER_SELECTION).click();
        By NEW_SUPPLIER_FIELD = byXpath("//li[contains(@aria-label,'"+newSupplier.getSupplierName()+"')]");
        $(NEW_SUPPLIER_FIELD).scrollTo().click();
        return this;
    }

    @Step("Validating ONT state and supplier")
    public OntUsageSupportPage checkOntDetails(Ont ont, Supplier supplier, String state){
        By ONT_CELL = byXpath("//td[@class='serial-cell']/a[contains(text(),'"+ont.getSerialNumber()+"')]");
        $(ONT_CELL).shouldBe(visible, Duration.ofMillis(3000)).click();
        $(DETAIL_STATE).shouldHave(text(state));
        $(DETAIL_SUPPLIER).shouldHave(text(supplier.getSupplierName()));
        return this;
    }

    @Step("validate insufficient permission errormessage")
    public void checkInsufficientPermissionsErrorMessage(){
        $(PERMISSION_DENIED_MSG).shouldBe(visible,Duration.ofMillis(2000));
    }

    @Step("Logout from Support UI")
    public void logout(){
        $(MENU_ICON).scrollTo().click();
        $(LOGOUT_BUTTON).shouldBe(visible, Duration.ofMillis(1000)).click();
        $(LOGIN_BUTTON).shouldBe(visible, Duration.ofMillis(2000)); //check that logout was successful
    }

    @Step("check supplier not visible")
    public OntUsageSupportPage checkSupplierNotVisible(Supplier supplier){
        $(SUPPLIER_SELECTION).click();
        By SUPPLIER = byXpath("//li[contains(@aria-label,'" + supplier.getSupplierName()+"')]");
        $(SUPPLIER).shouldNot(exist);
        return this;
    }

    @Step("check supplier is visible")
    public OntUsageSupportPage checkSupplierIsVisible(Supplier supplier){
        $(SUPPLIER_SELECTION).click();
        By SUPPLIER = byXpath("//li[contains(@aria-label,'"+supplier.getSupplierName()+"')]");
        $(SUPPLIER).should(exist);
        return this;
    }
}
