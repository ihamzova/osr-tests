package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_UI_MS;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

@Slf4j
@Getter
public class A4InventarSuchePage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-inventory-browser/inventory-search";

    public static final By A4_SEARCH_PAGE_HEADER = byXpath("/html/body/app-root/div/app-portal/app-page-header-v2/div[1]");

    // ops checkboxes
    @Getter
    public static final By WORKING_CHECKBOX_LOCATOR = By.id("opsWORKING");

    @Getter
    public static final By OPS_INSTALLING_CHECKBOX_LOCATOR = By.id("opsINSTALLING");

    @Getter
    public static final By NOT_WORKING_CHECKBOX_LOCATOR = By.id("opsNOT_WORKING");

    @Getter
    public static final By NOT_MANAGEABLE_CHECKBOX_LOCATOR = By.id("opsNOT_MANAGEABLE");

    @Getter
    public static final By FAILED_CHECKBOX_LOCATOR = By.id("opsFAILED");

    @Getter
    public static final By ACTIVATING_CHECKBOX_LOCATOR = By.id("opsACTIVATING");

    @Getter
    public static final By DEACTIVATING_CHECKBOX_LOCATOR = By.id("opsDEACTIVATING");


    // lcs checkboxes
    @Getter
    public static final By PLANNING_CHECKBOX_LOCATOR = By.id("lcsPLANNING");

    @Getter
    public static final By LIFECYCLE_INSTALLING_CHECKBOX_LOCATOR = By.id("lcsINSTALLING");

    @Getter
    public static final By OPERATING_CHECKBOX_LOCATOR = By.id("lcsOPERATING");

    @Getter
    public static final By RETIRING_CHECKBOX_LOCATOR = By.id("lcsRETIRING");

    // network element
    @Getter
    public static final By NE_CHOOSE_BUTTON_LOCATOR = By.id("searchNetworkElement");

    @Getter
    public static final By NE_VPSZ_FIELD_LOCATOR = By.id("vpsz");

    @Getter
    public static final By NE_AKZ_FIELD_LOCATOR = By.id("akz");

    @Getter
    public static final By NE_ONKZ_FIELD_LOCATOR = By.id("onkz");

    @Getter
    public static final By NE_VKZ_FIELD_LOCATOR = By.id("vkz");

    @Getter
    public static final By NE_FSZ_FIELD_LOCATOR = By.id("fsz");

    @Getter
    public static final By NE_CATEGORY_FIELD_LOCATOR = By.id("category");

    // value=<leer>, OLT, LEAF_SWITCH, SPINE_SWITCH, POD_SERVER, BOR

    @Getter
    //public static final By NE_SEARCH_BUTTON_LOCATOR = byXpath("/html/body/app-root/div/app-portal/div/app-inventory-search/div[1]/div[2]/app-search-networkelement/form/div[2]/div/button");
    //public static final By NE_SEARCH_BUTTON_LOCATOR = byClassName("btn w3-button w3-white w3-border w3-round-large border-telekom berlinium-input"); // funzt nicht
    //public static final By NE_SEARCH_BUTTON_LOCATOR = byClassName("w3-col w3-container m5 l5 w3-padding"); // funzt nicht
    //public static final By NE_SEARCH_BUTTON_LOCATOR = byClassName("w3-row"); // ja, geht; es wird aber keine Tabelle angezeigt
    // type="submit"
    //public static final By NE_SEARCH_BUTTON_LOCATOR = byText("btn w3-button w3-white w3-border w3-round-large border-telekom berlinium-input"); // geht nicht
    //public static final By NE_SEARCH_BUTTON_LOCATOR = byText("submit"); // geht nicht
    //public static final By NE_SEARCH_BUTTON_LOCATOR = byId("submit"); // geht nicht
    //public static final By NE_SEARCH_BUTTON_LOCATOR = byTitle("submit"); // geht nicht
    //public static final By NE_SEARCH_BUTTON_LOCATOR = By.id("button");  // geht nicht
    //public static final By NE_SEARCH_BUTTON_LOCATOR = By.linkText("button");  // geht nicht
    //public static final By NE_SEARCH_BUTTON_LOCATOR = By.className("btn w3-button w3-white w3-border w3-round-large border-telekom berlinium-input");  // geht nicht
    //public static final By NE_SEARCH_BUTTON_LOCATOR = By.className("btn w3-button w3-white w3-border w3-round-large border-telekom berlinium-input");  //

    public static final By NE_SEARCH_BUTTON_LOCATOR = By.id("searchSubmit");  // id="searchSubmit"

    // network element group
    @Getter
    //public static final By NEG_CHOOSE_BUTTON_LOCATOR = byXpath("/html/body/app-root/div/app-portal/div/app-inventory-search/form/div[1]/div[1]/p");
    public static final By NEG_CHOOSE_BUTTON_LOCATOR = By.id("searchNetworkElementGroup");

    @Getter
    public static final By NEG_NAME_INPUT_FIELD_LOCATOR = By.id("negName");

    @Getter
    //alt: public static final By SEARCH_BUTTON_LOCATOR = By.xpath("//*[@id=\"searchButton\"]");
    public static final By NEG_SEARCH_BUTTON_LOCATOR = By.id("negSearchSubmit");

    @Getter
    //public static final By SEARCH_RESULT_TABLE_LOCATOR = By.xpath("/html/body/app-root/div/app-portal/div/app-inventory-search/div/app-inventory-search-result/div/table");
    public static final By SEARCH_RESULT_TABLE_LOCATOR = By.id("searchResultsNetworkElementGroup");


    // common
    @Step("Validate page")
    public A4InventarSuchePage validate() {
        $(A4_SEARCH_PAGE_HEADER).waitUntil(visible, 3000);
        assertContains(url(), ENDPOINT);
        return this;
    }
    @Step("Login")
    public static A4InventarSuchePage login() {
        URL url = new OCUrlBuilder(A4_RESOURCE_INVENTORY_UI_MS).withEndpoint(ENDPOINT).build();
        return open(url, A4InventarSuchePage.class);
    }
}
