package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import lombok.Getter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_UI_MS;

public class A4ResourceOrderDetailPage {


    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-resource-order-browser/resource-order";
    public static final By A4_RO_DETAIL_PAGE_HEADER = byXpath("/html/body/app-root/div/app-portal/app-page-header-v3/div[1]");


    @Getter
    public static final By RO_ID_FIELD_LOCATOR = By.id("roId");

    @Getter
    public static final By RO_VUEP_FIELD_LOCATOR = By.id("vuepPublicReferenceNumber");

    @Getter
    public static final By RO_EXT_ORDER_ID_FIELD_LOCATOR = By.id("externalOrderId");

    @Getter
    public static final By RO_CBR_FIELD_LOCATOR = By.id("carrierBsaReferenz");

    @Getter
    public static final By RO_RVNR_FIELD_LOCATOR = By.id("rahmenvertragsnummer");

    @Getter
    public static final By RO_START_DATE_FIELD_LOCATOR = By.id("startDate");

    @Getter
    public static final By RO_COMPLETION_DATE_FIELD_LOCATOR = By.id("completionDate");

    @Getter
    public static final By RO_ORDER_DATE_FIELD_LOCATOR = By.id("orderDate");

    @Getter
    public static final By RO_STATUS_FIELD_LOCATOR = By.id("status");

    @Getter
    public static final By ROI_TABLE_LOCATOR = By.id("roItemTable");


}
