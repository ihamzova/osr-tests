package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import lombok.Getter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_UI_MS;

public class A4ResourceOrderDetailPage {


    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-resource-order-browser/resource-order";
    public static final By A4_RO_DETAIL_PAGE_HEADER = byXpath("/html/body/app-root/div/app-portal/app-page-header-v3/div[1]");

    @Getter
    public static final By A10NSP_CHOOSE_BUTTON_LOCATOR = By.id("searchA10NSP");

    @Getter
    public static final By RO_ID_FIELD_LOCATOR = By.id("roId");

    @Getter
    public static final By RO_PUBLIC_REFERENCE_ID_FIELD_LOCATOR = By.id("publicReferenceId");

    @Getter
    public static final By RO_EXT_ORDER_ID_FIELD_LOCATOR = By.id("externalOrderId");

    @Getter
    public static final By RO_CBR_FIELD_LOCATOR = By.id("carrierBsaReference");

    @Getter
    public static final By RO_FRAME_CONTRACT_ID_FIELD_LOCATOR = By.id("frameContractId");

    @Getter
    public static final By RO_START_DATE_FIELD_LOCATOR = By.id("startDate");

    @Getter
    public static final By RO_COMPLETION_DATE_FIELD_LOCATOR = By.id("completionDate");

    @Getter
    public static final By RO_ORDER_DATE_FIELD_LOCATOR = By.id("orderDate");

    @Getter
    public static final By RO_STATUS_FIELD_LOCATOR = By.id("status");

    @Getter
    public static final By ROI_TABLE_LOCATOR = By.id("roTableId");



    @Getter
    public static final By  CONTAINER_MAIN_DATA_A10NSP_RO = By.id("A10NSPResourceOrderMainData");


}
