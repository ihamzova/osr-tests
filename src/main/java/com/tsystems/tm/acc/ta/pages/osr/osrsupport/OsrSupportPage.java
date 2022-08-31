package com.tsystems.tm.acc.ta.pages.osr.osrsupport;

import com.codeborne.selenide.Condition;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.net.URL;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class OsrSupportPage {

    WebDriver driver;
    private static final Integer LATENCY_FOR_PROVISIONING_TO_FAIL = 150_000;

    private static final String APP = "osr-support-ui";
    private static final String ENDPOINT = "/scripts";
    private static final By DELETE_FTTH_LINES = byQaData("si-force-delete-al-by-endSz-a");
    private static final By ENDSZ_INPUT = byQaData("f-endSz-input");
    private static final By CHECK_CONSISTENCY_INPUT = byQaData("f-checkConsistency-input");
    private static final By EXECUTION_BUTTON = byQaData("f-execute-button");
    private static final By DELETED_STATUS = byText("Delete FTTH 1.7 Access Lines by OLT EndSz");
    private static final By FTTH_DESCRIPTION = byQaData("f-description-p");
    private static final By SCRIPT_STATE = byQaData("sp-script-state-dd");
    private static final By OLT_FORCE_DELETE = byQaData("si-olt-force-delete-by-endSz-a");
    private static final By CONSISTENCY = byQaData("f-checkConsistency-input");
    private static final By RECURSIVE_DELETE = byQaData("f-recursiveDelete-input");
    private static final By SCRIPT_ACTION = byQaData("sp-script-action-b");
    private static final By DELETE_BY_LINEID = byQaData("si-delete-al-by-lineId-a");
    private static final By LINEID = byQaData("f-lineId-input");
    private static final By DELETE_ANCP_SESSION = byQaData("si-ancp-session-delete-a");
    private static final By CLEANUP_ACCESSLINES = byQaData("si-al-cleanup-script-a");
    private static final By SLOT_INPUT = byQaData("f-slotNumber-input");
    private static final By PORT_INPUT = byQaData("f-portNumber-input");
    private static final By CHANGE_PROFILES = byQaData("si-migrate-al-nl-profiles-a");
    private static final By CARRIERCODE_INPUT = byQaData("f-carrierCode-input");
    private static final By TECHNOLOGY_INPUT = byQaData("f-technology-input");
    private static final By PRODUCTION_INPUT = byQaData("f-productionPlatform-input");
    private static final By RMKENDPOINTID = byQaData("f-rmkEndpointId-input");
    private static final By RMKACCESSID = byQaData("f-rmkEndpointId-input");
    private static final By MODIFY_RMKENDPOINT = byQaData("si-modify-rmk-endpoint-id-access-id-by-endSz-a");
    private static final By DELETE_SUBNET = byQaData("si-ancp-ip-subnet-delete-a");
    private static final By TOPAS_MIGRATION = byQaData("si-wholesale-migration-script-a");


    @Step("Open Osr Support page")
    public static OsrSupportPage openPage() {
        URL url = new GigabitUrlBuilder(APP).withEndpoint(ENDPOINT).build();
        log.info("Opening url " + url.toString());
        return open(url, OsrSupportPage.class);
    }

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Delete ftth lines")
    public OsrSupportPage deleteFtthLinesByOlt(PortProvisioning port) {
        $(DELETE_FTTH_LINES).click();
        $(FTTH_DESCRIPTION).shouldHave(Condition.text("Script for deleting RMK Access Lines from OLT Device by EndSz"));
        $(ENDSZ_INPUT).val(port.getEndSz());
        $(EXECUTION_BUTTON).click();
        $(SCRIPT_ACTION).shouldHave(Condition.text("force_delete_al_by_endSz"));
        $(SCRIPT_STATE).shouldHave(Condition.text("Finished"));

        return this;
    }

    @Step("Force delete of Olt by Endsz")
    public OsrSupportPage deleteOltByEndsz(PortProvisioning port, String consistency, String recursiveDelete) {
        $(OLT_FORCE_DELETE).shouldBe(Condition.visible).click();
        $(ENDSZ_INPUT).val(port.getEndSz());
        $(CHECK_CONSISTENCY_INPUT).val(consistency);
        $(RECURSIVE_DELETE).val(recursiveDelete);
        $(EXECUTION_BUTTON).click();
        $(SCRIPT_ACTION).shouldHave(Condition.text("olt_force_delete_by_endSz"));
        $(SCRIPT_STATE).shouldHave(Condition.text("Finished"));
        return this;
    }

    @Step("Delete Access Line by Line ID")
    public OsrSupportPage deleteAccessLineByLineId(String lineId) {
        $(DELETE_BY_LINEID).shouldBe(Condition.visible).click();
        $(LINEID).val(lineId);
        $(EXECUTION_BUTTON).click();
        $(SCRIPT_ACTION).shouldHave(Condition.text("delete_al_by_lineId "));
        $(SCRIPT_STATE).shouldHave(Condition.text("Finished"));
        return this;
    }

    @Step("Delete ANCP Session by EndSz")
    public OsrSupportPage deleteAncpSession(PortProvisioning port) {
        $(DELETE_ANCP_SESSION).shouldBe(Condition.visible).click();
        $(ENDSZ_INPUT).val(port.getEndSz());
        $(EXECUTION_BUTTON).click();
        $(SCRIPT_ACTION).shouldHave(Condition.text("ancp_session_delete"));
        $(SCRIPT_STATE).shouldHave(Condition.text("Finished"));
        return this;
    }

    @Step("Modify rmkEndpointId and rmkAccessId by endSz")
    public OsrSupportPage modifyRmkEndpointID(PortProvisioning port) {
        $(MODIFY_RMKENDPOINT).shouldBe(Condition.visible).click();
        $(ENDSZ_INPUT).val(port.getEndSz());
        $(RMKENDPOINTID).val(port.getEndSz());
        $(RMKACCESSID).val(port.getEndSz());
        $(EXECUTION_BUTTON).click();
        $(SCRIPT_ACTION).shouldHave(Condition.text("modify_rmk_endpoint_id_access_id_by_endSz"));
        $(SCRIPT_STATE).shouldHave(Condition.text("Finished"));
        return this;
    }

    @Step("Delete ANCP Session by EndSz")
    public OsrSupportPage cleanupAccessLines(PortProvisioning port) {
        $(CLEANUP_ACCESSLINES).shouldBe(Condition.visible).click();
        $(ENDSZ_INPUT).val(port.getEndSz());
        $(SLOT_INPUT).val(port.getSlotNumber());
        $(PORT_INPUT).val(port.getPortNumber());
        $(EXECUTION_BUTTON).click();
        $(SCRIPT_ACTION).shouldHave(Condition.text(""));
        $(SCRIPT_STATE).shouldHave(Condition.text("Finished"));
        return this;
    }

    @Step("Deleting ANCP IPSubnet by EndSz")
    public OsrSupportPage deleteIpSubnet(PortProvisioning port) {
        $(DELETE_SUBNET).shouldBe(Condition.visible).click();
        $(ENDSZ_INPUT).val(port.getEndSz());
        $(EXECUTION_BUTTON).click();
        $(SCRIPT_ACTION).shouldHave(Condition.text("ancp_ip_subnet_delete"));
        $(SCRIPT_STATE).shouldHave(Condition.text("Finished"));
        return this;
    }

    @Step("TOPAS access lines migration")
    public OsrSupportPage migrateTopasAccessLines(PortProvisioning port) {
        $(TOPAS_MIGRATION).shouldBe(Condition.visible).click();
        $(ENDSZ_INPUT).val(port.getEndSz());
        $(EXECUTION_BUTTON).click();
        $(SCRIPT_ACTION).shouldHave(Condition.text("wholesale_migration_script"));
        $(SCRIPT_STATE).shouldHave(Condition.text("Finished"));
        return this;
    }

    @Step("Change Network Line Profiles for 1:64 migration")
    public OsrSupportPage changeNetworkLineProfiles(String carrierCode, String technology, String platform) {
        $(CHANGE_PROFILES).shouldBe(Condition.visible).click();
        $(CARRIERCODE_INPUT).val(carrierCode);
        $(TECHNOLOGY_INPUT).val(technology);
        $(PRODUCTION_INPUT).val(platform);
        $(EXECUTION_BUTTON).click();
        $(SCRIPT_ACTION).shouldHave(Condition.text("migrate_al_nl_profiles"));
        $(SCRIPT_STATE).shouldHave(Condition.text("Finished"));
        return this;
    }

}
