package com.tsystems.tm.acc.ta.pages.osr.dpucommissioning;

import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static com.tsystems.tm.acc.ta.util.AsyncAssert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class DpuInfoPage {

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT = "/detail";
    public static final Integer MAX_LATENCY_FOR_ELEMENT_APPEARS = 2000;  // rendering delay
    public static final Integer MAX_LATENCY_FOR_LIFECYCLE_CHANGE = 5000;
    private static final Integer TIMEOUT_FOR_DPU_COMMISSIONING = 10 * 60_000;


    private static final By DEVICE_LIFE_CYCLE_STATE_LOCATOR = byQaData("device_lifecyclestate");
    public static final By PON_PORT_LIFE_CYCLE_STATE_LOCATOR = byQaData("port_1_pon_lifecyclestate");

    public static final By DPU_PORTS_VIEW_TAB_LOCATOR = byQaData("a-ports-view");
    public static final By DPU_CONFIGURATION_VIEW_TAB_LOCATOR = byQaData("a-configuration-view");
    public static final By DPU_ACCESS_LINES_VIEW_TAB_LOCATOR = byQaData("a-access-lines-view");

    public static final By START_DPU_COMMISSIONING_BUTTON_LOCATOR = byQaData("dpu_commissioning_start");
    public static final By ETCD_BUSINESS_KEY = byQaData("DPU_COMMISSIONING");
    public static final By START_DPU_DECOMMISSIONING_BUTTON_LOCATOR = byQaData("dpu_decommissioning_start");
    public static final By ETCD_DECOMMISSIONING_BUSINESS_KEY = byQaData("DPU_DE_COMMISSIONING");

    public static final By DEVICE_FUNCTION_BUTTON_LOCATOR = byQaData("device_functions");
    public static final By EDIT_DPU_DEVICE_BUTTON_LOCATOR_0 = byQaData("device_functions_option_0");
    public static final By EDIT_DPU_DEVICE_BUTTON_LOCATOR_1 = byQaData("device_functions_option_1");
    public static final By START_EDIT_DPU_DEVICE_BUTTON_LOCATOR = byQaData("device_functions_action");
    public static final By DELETE_DPU_DEVICE_BUTTON_LOCATOR = byQaData("device_delete_perform");

    private String businessKey; // check etcd values

    // verify
    public static final By DPU_KLS_ID_LOCATOR = byQaData("span-olt-klsid");
    public static final By DPU_ANCP_CONFIGURATION_STATE_LOCATOR = byQaData("dpu_ancp_session_status");
    public static final By OLT_EMS_CONFIGURATION_STATE_LOCATOR = byQaData("olt_ems_configuration_status");
    public static final By OLT_EMS_DPU_ENDSZ_LOCATOR = byQaData("olt_ems_dpu_endsz");
    public static final By OLT_EMS_OLT_ENDSZ_LOCATOR = byQaData("olt_ems_olt_endsz");
    public static final By DPU_EMS_CONFIGURATION_STATE_LOCATOR = byQaData("dpu_ems_configuration_status");
    public static final By DPU_EMS_DPU_ENDSZ_LOCATOR = byQaData("dpu_ems_dpu_endsz");


    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP,  CommonHelper.commonTimeout.intValue());
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout.intValue());
    }

    @Step("Start dpu commissioning")
    public DpuInfoPage startDpuCommissioning() {
        $(START_DPU_COMMISSIONING_BUTTON_LOCATOR).click();
        //check DPU COMMISSIONING PROCESS and catch businessKey
        businessKey = $(ETCD_BUSINESS_KEY).should(exist, Duration.ofMillis(MAX_LATENCY_FOR_LIFECYCLE_CHANGE)).getValue();
        log.info("startDpuCommissioning() businessKey = {}", businessKey);
        $(DEVICE_LIFE_CYCLE_STATE_LOCATOR).should(exactTextCaseSensitive(DevicePortLifeCycleStateUI.INSTALLING.toString()), Duration.ofMillis(MAX_LATENCY_FOR_LIFECYCLE_CHANGE));
        log.info("get device life cycle state = {}", getDeviceLifeCycleState());
        $(START_DPU_COMMISSIONING_BUTTON_LOCATOR).should(appear, Duration.ofMillis(TIMEOUT_FOR_DPU_COMMISSIONING));
        return this;
    }

    @Step("Start dpu decommissioning")
    public DpuInfoPage startDpuDecommissioning() {
        $(START_DPU_DECOMMISSIONING_BUTTON_LOCATOR).click();
        //check DPU COMMISSIONING PROCESS and catch businessKey
        businessKey = $(ETCD_DECOMMISSIONING_BUSINESS_KEY).should(exist, Duration.ofMillis(MAX_LATENCY_FOR_LIFECYCLE_CHANGE)).getValue();
        log.info("startDpuDecommissioning() businessKey = {}", businessKey);
        sleep(MAX_LATENCY_FOR_ELEMENT_APPEARS);
        //$(DEVICE_LIFE_CYCLE_STATE_LOCATOR).should(exactTextCaseSensitive(DevicePortLifeCycleStateUI.RETIRING.toString()), Duration.ofMillis(MAX_LATENCY_FOR_LIFECYCLE_CHANGE));
        log.info("dpu decommissioning get device life cycle state = {}", getDeviceLifeCycleState());
        $(START_DPU_COMMISSIONING_BUTTON_LOCATOR).should(appear, Duration.ofMillis(TIMEOUT_FOR_DPU_COMMISSIONING));
        return this;
    }

    @Step("Open DPU Ports Tab")
    public DpuInfoPage openDpuPortsTab() {
        $(DPU_PORTS_VIEW_TAB_LOCATOR).click();
        return this;
    }

    @Step("Open DPU Konfiguration Tab. Check DPU Verschaltung")
    public DpuInfoPage openDpuConfiguraionTab() {
        $(DPU_CONFIGURATION_VIEW_TAB_LOCATOR).click();
        return this;
    }

    @Step("Open DPU Access-Lines Tab")
    public DpuInfoPage openDpuAccessLinesTab() {
        $(DPU_ACCESS_LINES_VIEW_TAB_LOCATOR).click();
        return this;
    }

    @Step("DPU editieren")
    public DpuInfoPage openDpuEditPage() {
        $(DEVICE_FUNCTION_BUTTON_LOCATOR).click();
        $(EDIT_DPU_DEVICE_BUTTON_LOCATOR_0).click();
        $(START_EDIT_DPU_DEVICE_BUTTON_LOCATOR).click();
        return this;
    }

    @Step("open DPU deletion dialog")
    public DpuInfoPage openDpuDeletionDialog() {
        $(DEVICE_FUNCTION_BUTTON_LOCATOR).click();
        $(EDIT_DPU_DEVICE_BUTTON_LOCATOR_1).click();
        $(START_EDIT_DPU_DEVICE_BUTTON_LOCATOR).click();
        return this;
    }

    public DpuInfoPage deleteDevice() {
        $(DELETE_DPU_DEVICE_BUTTON_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
        sleep(2000); // waiting to save in the inventory database
        return this;
    }



    @Step("Get device life cycle state")
    public static String getDeviceLifeCycleState() {
        return $(DEVICE_LIFE_CYCLE_STATE_LOCATOR).getText();
    }

    @Step("Get port life cycle state")
    public static String getPortLifeCycleState() {
        return $(PON_PORT_LIFE_CYCLE_STATE_LOCATOR).getText();
    }

    @Step("get businessKey")
    public String getBusinessKey() {
        return businessKey;
    }

    @Step("get displayed KlsId")
    public static String getDpuKlsId() {
        return $(DPU_KLS_ID_LOCATOR).getText();
    }

    @Step("get dpu ancp config state")
    public static String getDpuAncpConfigState() {
        log.info("dpu ancp config state is {} ", $(DPU_ANCP_CONFIGURATION_STATE_LOCATOR).getText());
        return $(DPU_ANCP_CONFIGURATION_STATE_LOCATOR).getText();
    }

    @Step("get olt ems config state")
    public static String getOltEmsConfigState() {
        log.info("olt ems config state is {} ", $(OLT_EMS_CONFIGURATION_STATE_LOCATOR).getText());
        return $(OLT_EMS_CONFIGURATION_STATE_LOCATOR).getText();
    }

    @Step("get olt_ems_dpu_endsz")
    public static String getOltEmsDpuEndsz() {
        log.info("olt ems dpu endsz is {} ", $(OLT_EMS_DPU_ENDSZ_LOCATOR).getText());
        return $(OLT_EMS_DPU_ENDSZ_LOCATOR).getText();
    }

    @Step("get olt_ems_olt_endsz")
    public static String getOltEmsOltEndsz() {
        return $(OLT_EMS_OLT_ENDSZ_LOCATOR).getText();
    }

    @Step("check dpu ems config state")
    public static String getDpuEmsConfigState() {
        log.info("dpu ems config state is {} ", $(DPU_EMS_CONFIGURATION_STATE_LOCATOR).getText());
        return $(DPU_EMS_CONFIGURATION_STATE_LOCATOR).getText();
    }

    @Step("get dpu_ems_dpu_endsz")
    public static String getDpuEmsDpuEndsz() {
        return $(DPU_EMS_DPU_ENDSZ_LOCATOR).getText();
    }
}
