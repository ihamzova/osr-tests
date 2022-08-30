package com.tsystems.tm.acc.ta.team.upiter.accesslinesearch;

import com.tsystems.tm.acc.data.upiter.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.pages.osr.accessmanagement.AccessLineSearchPage;
import com.tsystems.tm.acc.ta.pages.osr.accessmanagement.AccessLinesManagementPage;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.*;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import groovy.util.logging.Slf4j;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.visible;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.*;

@Slf4j
@Epic("Access Management Support UI")
@ServiceLog({
        ACCESS_MANAGEMENT_SUPPORT_UI_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        ACCESS_LINE_BFF_PROXY_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        WG_ACCESS_PROVISIONING_MS,
        ONT_OLT_ORCHESTRATOR_MS
})
public class AccessLinesSearchTest extends GigabitTest {

    private AccessLineRiRobot accessLineRiRobot;
    private WgAccessProvisioningRobot wgAccessProvisioningRobot;
    private UpiterTestContext context = UpiterTestContext.get();
    private PortProvisioning accessLinesByEndSz;
    private PortProvisioning accessLinesByEndSzSlotPort;
    private AccessLine accessLine;
    private Credentials loginData;

    @BeforeSuite
    public void beforeSuite() {
        accessLineRiRobot = new AccessLineRiRobot();

        accessLineRiRobot.clearDatabaseByOlt("49/89/8000/76H2");
        accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H1");
        accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H3");
        accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H5");
        accessLineRiRobot.clearDatabaseByOlt("49/30/179/76H1");
        accessLineRiRobot.clearDatabaseByOlt("49/30/179/76G3");
    }

    @BeforeClass
    public void init() throws InterruptedException {
        accessLineRiRobot.fillDatabaseForOltCommissioningWithDpu(true, AccessTransmissionMedium.TWISTED_PAIR, 1, 1, "49/89/8000/76H2",
                "49/812/179/71G0", "1", "0");

        wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
        wgAccessProvisioningRobot.changeFeatureToggleHomeIdPoolState(false);
        accessLine = new AccessLine();
        accessLinesByEndSz = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.searchByEndSz);
        accessLinesByEndSzSlotPort = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.searchByEndSzSlotPort);
    }

    @BeforeMethod
    void setup() {
        loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOTelekomNSOOpsRW);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    @Test
    @TmsLink("DIGIHUB-39501")
    @Description("Search by EndSz, check for basic information")
    public void searchAccessLinesByEndSzTest() {
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSzSlotPort)
                .clickSearchButton();

        accessLineSearchPage.checkTableHeaders(accessLineSearchPage.getTableHeaders());
        accessLineSearchPage.checkTableMessagePattern(accessLineSearchPage.getTableMessage());
        accessLineSearchPage.checkPaginationSizes(accessLineSearchPage.getPaginatorSizes());
        accessLineSearchPage.setStatus("Walled Garden").searchAccessLinesByPortAddress(accessLinesByEndSzSlotPort);
        accessLineSearchPage.checkBasicInformation();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.checkAccessLineProfilesStates("ACTIVE", "NULL",
                "ACTIVE", "NULL");
    }

    @Test
    @TmsLink("DIGIHUB-39501")
    @Description("Check sort by endSz, slot number and port number")
    public void sortingBySlotAndPortNumbersTest() {
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSz).clickSearchButton();
        accessLineSearchPage.setPageSize(50);
        accessLineSearchPage.getTableRows(50);
        accessLineSearchPage.checkSortOfTable(accessLineSearchPage.getAccessLinesFromTable());
    }

    @Test
    @TmsLink("DIGIHUB-39499")
    @Description("Search by HomeID, check for basic information")
    public void searchAccessLinesByHomeIdTest() {
        String homeId = accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
                        AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, ProfileState.ACTIVE)
                .stream().filter(accessLineDto -> accessLineDto.getHomeId()!=null)
                .collect(Collectors.toList())
                .get(0).getHomeId();
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByHomeID(homeId).clickSearchButton();
        accessLineSearchPage.checkBasicInformation();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.checkAccessLineProfilesStates("INACTIVE", "ACTIVE",
                "INACTIVE", "ACTIVE");
    }

    @Test
    @TmsLink("DIGIHUB-39491")
    @Description("Search by LineID, check for basic information, inactive line")
    public void searchAccessLinesByLineIdTest() {
        accessLine.setLineId(accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
                        AccessLineTechnology.GPON, AccessLineStatus.INACTIVE, null, null)
                .get(0).getLineId());
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId()).clickSearchButton();
        accessLineSearchPage.checkBasicInformation();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.checkAccessLineProfilesStates("ACTIVE", "NULL",
                "NULL", "NULL");
    }

    @Test
    @TmsLink("DIGIHUB-66818")
    @Description("Search by KLS ID")
    public void searchAccessLinesByKlsIdTest() {
        String klsId = accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
                        AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, ProfileState.ACTIVE)
                .get(0).getSubscriberNetworkLineProfile().getKlsId().toString();
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByKlsId(klsId).clickSearchButton();
        accessLineSearchPage.checkBasicInformation();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.checkAccessLineProfilesStates("INACTIVE", "ACTIVE",
                "INACTIVE", "ACTIVE");
    }

    @Test
    @TmsLink("DIGIHUB-66819")
    @Description("Search by ONT S/N")
    public void searchAccessLinesByOntSnTest() {
        String ontSerialNumber = accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
                        AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, ProfileState.ACTIVE)
                .get(0).getDefaultNeProfile().getSubscriberNeProfile().getOntSerialNumber();
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByOntSn(ontSerialNumber).clickSearchButton();
        accessLineSearchPage.checkBasicInformation();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.checkAccessLineProfilesStates("INACTIVE", "ACTIVE",
                "INACTIVE", "ACTIVE");
    }

    @Test
    @TmsLink("DIGIHUB-39505")
    @Description("Search access line by EndSZ and multiple statuses in Support UI")
    public void searchAccessLinesByEndSzFilteringTest() {
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSz)
                .setStatus("Walled Garden")
                .setStatus("Inactive");
        accessLineSearchPage.checkBasicInformation();
        assertTrue(accessLineSearchPage.sortIconIsPresentInStatusColumn(), "Sort icon is not present in status column");
        accessLineSearchPage.sortAccessLinesByStatus()
                .setPageSize(10);
        accessLineSearchPage.getTableRows(10);
        assertEquals(accessLineSearchPage.getAccessLinesFromTable().get(0).getStatus(), AccessLineStatus.INACTIVE, "Table wasn't sorted");
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.checkAccessLineProfilesStates("ACTIVE", "NULL",
                "NULL", "NULL");
    }

    @Test
    @TmsLink("DIGIHUB-75668")
    @Description("Search for ftth olt_bng out_of_sync access lines, subscriber_ne_profile is out_of_sync")
    public void searchFtthOltBngAccessLineOutOfSyncSubscriberNeProfileTest() {
        AccessLineDto accessLine = accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
                AccessLineTechnology.GPON, AccessLineStatus.WALLED_GARDEN, null, null).get(0);

        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId())
                .clickSearchButton()
                .checkBasicInformation();
        AccessLinesManagementPage accessLinesManagementPage =
                accessLineSearchPage.clickMagnifyingGlassForLine(accessLine.getLineId())
                .clickEditButton()
                .changeDefaultNeProfileState("INACTIVE")
                .addSubscriberNeProfile("ACTIVE", "ONLINE")
                .changeAccessLineStatus("ASSIGNED")
                .clickSaveLocallyButton();

        accessLinesManagementPage.getUnsynchronTooltip("ne-profile-subscriber").shouldBe(visible);
        accessLinesManagementPage.closeCurrentTab();

        accessLinesManagementPage.returnToAccessLinesSearchPage()
                .turnOnOutOfSyncSwitch()
                .clickSearchButton()
                .checkBasicInformation();

        assertTrue(accessLineSearchPage.getAccessLinesFromTable().stream().allMatch(accessLineViewDto -> accessLineViewDto.getOutOfSync().equals("OUT_OF_SYNC")),
                "Some of the AccessLines don't have an out_of_sync sign");
        assertEquals(accessLineSearchPage.getAccessLineFromTableByLineId(accessLine.getLineId()).size(), 1, "Out_of_sync AccessLine was not found");

        accessLinesManagementPage = accessLineSearchPage.clickUnsynchronSignForLine(accessLine.getLineId());
        accessLinesManagementPage.getUnsynchronTooltip("ne-profile-subscriber").shouldBe(visible);
        accessLinesManagementPage.checkAccessLineProfilesStates("INACTIVE", "ACTIVE",
                "ACTIVE", "NULL");
    }

    @Test
    @TmsLink("DIGIHUB-75668")
    @Description("Search for ftth olt_bng out_of_sync access lines, subscriber_networkline_profile is out_of_sync")
    public void searchFtthOltBngAccessLineOutOfSyncSubscriberNlProfileTest() {
        AccessLineDto accessLine = accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
                AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, null).get(0);

        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId())
                .clickSearchButton()
                .checkBasicInformation();
        AccessLinesManagementPage accessLinesManagementPage =
                accessLineSearchPage.clickMagnifyingGlassForLine(accessLine.getLineId())
                        .clickEditButton()
                        .changeDefaultNlProfileState("INACTIVE")
                        .addSubscriberNlProfile("98765", "ACTIVE")
                        .clickSaveLocallyButton();

        accessLinesManagementPage.getUnsynchronTooltip("nl-profile-subscriber").shouldBe(visible);
        accessLinesManagementPage.closeCurrentTab();

        accessLinesManagementPage.returnToAccessLinesSearchPage()
                .turnOnOutOfSyncSwitch()
                .clickSearchButton()
                .checkBasicInformation();

        assertTrue(accessLineSearchPage.getAccessLinesFromTable().stream().allMatch(accessLineViewDto -> accessLineViewDto.getOutOfSync().equals("OUT_OF_SYNC")),
                "Some of the AccessLines don't have an out_of_sync sign");
        assertEquals(accessLineSearchPage.getAccessLineFromTableByLineId(accessLine.getLineId()).size(), 1, "Out_of_sync AccessLine was not found");

        accessLinesManagementPage = accessLineSearchPage.clickUnsynchronSignForLine(accessLine.getLineId());
        accessLinesManagementPage.getUnsynchronTooltip("nl-profile-subscriber").shouldBe(visible);
        accessLinesManagementPage.checkAccessLineProfilesStates("INACTIVE", "ACTIVE",
                "INACTIVE", "ACTIVE");
    }

    @Test
    @TmsLink("DIGIHUB-75668")
    @Description("Search for ftth a4 out_of_sync access lines")
    public void searchOutOfSyncFtthA4AccessLinesTest() {
        AccessLineDto accessLine = accessLineRiRobot.getA4AccessLinesWithOnt(AccessLineTechnology.GPON).get(0);

        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId())
                .clickSearchButton()
                .checkBasicInformation();

        AccessLinesManagementPage accessLinesManagementPage;

        if (accessLine.getSubscriberNetworkLineProfile() == null) {
            accessLinesManagementPage =
                    accessLineSearchPage.clickMagnifyingGlassForLine(accessLine.getLineId())
                            .clickEditButton()
                            .changeSerialNumberOnNsp("9995874854785478")
                            .changeAccessLineStatus("WALLED_GARDEN")
                            .clickSaveLocallyButton();
        } else {
            accessLinesManagementPage =
                    accessLineSearchPage.clickMagnifyingGlassForLine(accessLine.getLineId())
                            .clickEditButton()
                            .changeSerialNumberOnNsp("9995874854785478")
                            .clickSaveLocallyButton();
        }

        accessLinesManagementPage.getUnsynchronTooltip("nsp-reference").shouldBe(visible);
        accessLinesManagementPage.closeCurrentTab();

        accessLinesManagementPage.returnToAccessLinesSearchPage()
                .turnOnOutOfSyncSwitch()
                .clickSearchButton()
                .checkBasicInformation();

        assertTrue(accessLineSearchPage.getAccessLinesFromTable().stream().allMatch(accessLineViewDto -> accessLineViewDto.getOutOfSync().equals("OUT_OF_SYNC")),
                "Some of the AccessLines don't have an out_of_sync sign");
        assertEquals(accessLineSearchPage.getAccessLineFromTableByLineId(accessLine.getLineId()).size(), 1, "Out_of_sync AccessLine was not found");

        accessLinesManagementPage = accessLineSearchPage.clickUnsynchronSignForLine(accessLine.getLineId());
        assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber(), "9995874854785478");
        accessLinesManagementPage.getUnsynchronTooltip("nsp-reference").shouldBe(visible);
    }

    @Test
    @TmsLink("DIGIHUB-75668")
    @Description("Search for fttb olt_bng out_of_sync access lines")
    public void searchOutOfSyncFttbOltBngAccessLinesTest() {
        AccessLineDto accessLine = accessLineRiRobot.getAccessLinesByType(AccessLineProductionPlatform.OLT_BNG, AccessLineTechnology.GFAST, AccessLineStatus.WALLED_GARDEN).get(0);
        String initialOnuAccessId = accessLine.getFttbNeProfile().getOnuAccessId().toString();

        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId())
                .clickSearchButton()
                .checkBasicInformation();
        AccessLinesManagementPage accessLinesManagementPage =
                accessLineSearchPage.clickMagnifyingGlassForLine(accessLine.getLineId())
                        .clickEditButton()
                        .generateOnuAccessId()
                        .clickSaveLocallyButton();

        String newOnuAccessId = accessLinesManagementPage.getOnuAccessId();
        assertNotEquals(newOnuAccessId, initialOnuAccessId);

        accessLinesManagementPage.getUnsynchronTooltip("fttb-ne-profile").shouldBe(visible);
        accessLinesManagementPage.closeCurrentTab();

        accessLinesManagementPage.returnToAccessLinesSearchPage()
                .turnOnOutOfSyncSwitch()
                .clickSearchButton()
                .checkBasicInformation();

        assertTrue(accessLineSearchPage.getAccessLinesFromTable().stream().allMatch(accessLineViewDto -> accessLineViewDto.getOutOfSync().equals("OUT_OF_SYNC")),
                "Some of the AccessLines don't have an out_of_sync sign");
        assertEquals(accessLineSearchPage.getAccessLineFromTableByLineId(accessLine.getLineId()).size(), 1, "Out_of_sync AccessLine was not found");

        accessLinesManagementPage = accessLineSearchPage.clickUnsynchronSignForLine(accessLine.getLineId());
        assertEquals(accessLinesManagementPage.getOnuAccessId(), newOnuAccessId);
        accessLinesManagementPage.getUnsynchronTooltip("fttb-ne-profile").shouldBe(visible);
    }

    @Test
    @TmsLink("DIGIHUB-81428")
    @Description("Add subscriber_ne_profile and Save and Reconfigure")
    public void addSubscriberNeProfileTest() {
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSzSlotPort)
                .setStatus("Walled Garden")
                .clickMagnifyingGlassForLine(0);
        String lineId = accessLinesManagementPage.getLineId();
        accessLinesManagementPage.clickEditButton()
                .addSubscriberNeProfile("ACTIVE", "ONLINE")
                .changeDefaultNeProfileState("INACTIVE")
                .changeAccessLineStatus("ASSIGNED")
                .clickSaveAndReconfigureButton()
                .closeCurrentTab();
        accessLinesManagementPage.returnToAccessLinesSearchPage()
                .searchAccessLinesByLineID(lineId)
                .clickSearchButton()
                .clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.checkAccessLineProfilesStates("INACTIVE", "ACTIVE",
                "ACTIVE", "NULL");
    }

    @Test
    @TmsLink("DIGIHUB-*****")
    @Description("Update Wholebuy AccessLine and Save and Reconfigure")
    public void saveAndReconfigureWholebuyAccessLineTest() {
        String accessNetworkType = "Ethernet";
        accessLine.setLineId(accessLineRiRobot
                .getAccessLinesByType(AccessLineProductionPlatform.WHOLEBUY, AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED).get(0).getLineId());

        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId()).clickSearchButton()
                .clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.clickEditButton()
                .changeAccessNetworkType(accessNetworkType)
                .clickSaveAndReconfigureButton();
        accessLinesManagementPage.closeCurrentTab();
        accessLinesManagementPage.returnToAccessLinesSearchPage()
                .searchAccessLinesByLineID(accessLine.getLineId())
                .clickSearchButton()
                .clickMagnifyingGlassForLine(0);

        assertEquals(accessLinesManagementPage.getAccessNetworkType(), accessNetworkType);
        accessLinesManagementPage.getUnsynchronTooltip("nl-profile-subscriber").shouldNotBe(visible);
        accessLinesManagementPage.checkAccessLineProfilesStates("NULL", "NULL",
                "NULL", "ACTIVE");
    }

    @Test
    @TmsLink("DIGIHUB-125083")
    @Description("Terminate Ftth AccessLine. AccessLine is set to Walled_Garden")
    public void terminateAssignedFtthAlTest() {
        accessLine.setLineId(accessLineRiRobot.getAccessLinesByType(AccessLineProductionPlatform.OLT_BNG, AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED).get(0).getLineId());
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId()).clickSearchButton()
                .clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.clickEditButton()
                .clickTerminationButton()
                .closeCurrentTab();
        accessLinesManagementPage.returnToAccessLinesSearchPage()
                .waitUntilNeededStatus("WALLED_GARDEN", accessLine.getLineId())
                .clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.checkAccessLineProfilesStates("ACTIVE", "NULL",
                "ACTIVE", "NULL");
        assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()), AccessLineStatus.WALLED_GARDEN,
                "AccessLine state is incorrect");
    }

    @Test
    @TmsLink("DIGIHUB-125016")
    @Description("Add serialnumber for A4 AccessLine and save locally")
    public void saveLocallyA4AlTest() {
        String ontSerialNumber = "9876543210987654";
        accessLine.setLineId(accessLineRiRobot.getAccessLinesByType(AccessLineProductionPlatform.A4, AccessLineTechnology.GPON, AccessLineStatus.WALLED_GARDEN).get(0).getLineId());
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId()).clickSearchButton()
                .clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.clickEditButton()
                .changeAccessLineStatus("ASSIGNED")
                .changeSerialNumberOnNsp(ontSerialNumber)
                .changeDefaultNlProfileState("INACTIVE")
                .addSubscriberNlProfile("123456", "ACTIVE")
                .clickSaveLocallyButton()
                .closeCurrentTab();
        accessLinesManagementPage.returnToAccessLinesSearchPage()
                .searchAccessLinesByLineID(accessLine.getLineId())
                .clickSearchButton()
                .clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.checkAccessLineProfilesStates("NULL", "NULL", "INACTIVE", "ACTIVE");
        assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber(), ontSerialNumber);
    }

    @Test
    @TmsLink("DIGIHUB-125017")
    @Description("Add Subscriber Profile for FTTB AccessLine and save locally")
    public void saveLocallyFttbALTest() {
        accessLine.setLineId(accessLineRiRobot.getFttbAccessLines(AccessTransmissionMedium.TWISTED_PAIR, AccessLineStatus.WALLED_GARDEN, AccessLineProductionPlatform.OLT_BNG).get(0).getLineId());
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId()).clickSearchButton()
                .clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.clickEditButton()
                .changeAccessLineStatus("ASSIGNED")
                .changeDefaultNlProfileState("INACTIVE")
                .addSubscriberNlProfile("123456", "ACTIVE")
                .clickSaveLocallyButton()
                .closeCurrentTab();
        accessLinesManagementPage.returnToAccessLinesSearchPage()
                .searchAccessLinesByLineID(accessLine.getLineId())
                .clickSearchButton()
                .clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.checkFTTBProfiles("INACTIVE", "ACTIVE", "ACTIVE", "ACTIVE");
    }

    @Test
    @TmsLink("DIGIHUB-96235")
    @Description("ONT Abmeldung, AccessLine is set to Walled_Garden")
    public void ontAbmeldungTest() {
        accessLine.setLineId(accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
                        AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, null)
                .get(0).getLineId());
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId()).clickSearchButton();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);
        String lineId = accessLinesManagementPage.getLineId();
        accessLinesManagementPage.clickEditButton()
                .clickOntAbmeldungButton()
                .clickBestätigenButton()
                .clickEditButton()
                .changeAccessLineStatus("WALLED_GARDEN")
                .clickSaveAndReconfigureButton()
                .closeCurrentTab();
        accessLinesManagementPage.returnToAccessLinesSearchPage()
                .searchAccessLinesByLineID(lineId)
                .clickSearchButton()
                .clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.checkAccessLineProfilesStates("ACTIVE", "NULL",
                "ACTIVE", "NULL");
    }

    @Test
    @TmsLink("DIGIHUB-153943")
    @Description("Set homeId to Null in Access Management UI")
    public void deleteHomeIdTest() {
        accessLine.setLineId(accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
                        AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, ProfileState.ACTIVE)
                .get(0).getLineId());
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId()).clickSearchButton();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);
        String lineId = accessLinesManagementPage.getLineId();
        accessLinesManagementPage.clickEditButton()
                .removeHomeID()
                .closeCurrentTab();
        accessLinesManagementPage.returnToAccessLinesSearchPage()
                .searchAccessLinesByLineID(lineId)
                .clickSearchButton()
                .clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.checkAccessLineProfilesStates("INACTIVE", "ACTIVE",
                "INACTIVE", "ACTIVE");
        assertNull(accessLineRiRobot.getAccessLinesByLineId(lineId).get(0).getHomeId());
    }

    @Test
    @TmsLink("DIGIHUB-55323")
    @Description("Search for Backhaul ID by EndSZ in Access Management UI")
    public void searchBackhaulIDsByEndsZTest() {
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchBackhaulIDs(accessLinesByEndSzSlotPort).clickSearchButton();
        accessLineSearchPage.checkBackhaulIdsTableHeaders(accessLineSearchPage.getTableHeaders());
        accessLineSearchPage.checkPaginationSizes(accessLineSearchPage.getPaginatorSizes());
        assertEquals(accessLineSearchPage.getTableRows().size(), 1);
    }

    @Test
    @TmsLink("DIGIHUB-55322")
    @Description("Search for Backhaul ID by Backhaul ID in Access Management UI")
    public void searchBackhaulIDByBackhaulIdTest() {
        String backhaulId = accessLineRiRobot.getBackHaulId(accessLinesByEndSzSlotPort).get(0).getBackhaulId();
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchBackhaulIDbyBackhaulId(backhaulId).clickSearchButton();
        accessLineSearchPage.checkBackhaulIdsTableHeaders(accessLineSearchPage.getTableHeaders());
        accessLineSearchPage.checkPaginationSizes(accessLineSearchPage.getPaginatorSizes());
        assertEquals(accessLineSearchPage.getTableRows().size(), 1);
    }

    @Test
    @TmsLink("DIGIHUB-126893")
    @Description("A4 connectivity test in Access Management UI")
    public void a4ConnectivityTest() {
        AccessLineDto a4AccessLine = accessLineRiRobot.getA4AccessLinesWithOnt(AccessLineTechnology.GPON).get(0);
        accessLine.setLineId(a4AccessLine.getLineId());
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.searchAccessLinesByLineID(a4AccessLine.getLineId()).clickSearchButton()
                .clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.clickEditButton().startA4ConnectivityTest();
    }

    @Test
    @TmsLink("DIGIHUB-126890")
    @Description("Ftth connectivity test in Access Management UI")
    public void ftthConnectivityTest() {
        AccessLineDto accessLine = accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
                        AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, ProfileState.ACTIVE)
                .get(0);
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId()).clickSearchButton()
                .clickMagnifyingGlassForLine(0);
        accessLinesManagementPage
                .clickEditButton()
                .changeOntState("OFFLINE")
                .clickSaveAndReconfigureButton()
                .waitUntilNeededStatus("OFFLINE")
                .clickEditButton()
                .startConnectivityTest()
                .closeNotificationButton()
                .clickAbbrechenButton();
        accessLinesManagementPage.returnToAccessLinesSearchPage()
                .searchAccessLinesByLineID(accessLine.getLineId())
                .clickSearchButton()
                .clickMagnifyingGlassForLine(0)
                .waitUntilNeededStatus("ONLINE");
        accessLinesManagementPage.checkAccessLineProfilesStates("INACTIVE", "ACTIVE",
                "INACTIVE", "ACTIVE");
        assertEquals(accessLinesManagementPage.getOntState(), OntState.ONLINE.toString());
    }

    @Test
    @TmsLink("DIGIHUB-85696")
    @Description("Generate Onu ID")
    public void generateOnuIdTest() {
        AccessLineDto accessLine = accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
                        AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, ProfileState.ACTIVE)
                .get(0);
        Integer initialOnuid = accessLineRiRobot.getAllocatedOnuIdFromAccessLine(accessLine).get(0);
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId()).clickSearchButton()
                .clickMagnifyingGlassForLine(0);

        accessLinesManagementPage
                .clickEditButton()
                .generateOnuID()
                .clickSaveAndReconfigureButton();
        assertNotEquals(accessLinesManagementPage.getOnuID(), initialOnuid.toString());
        accessLinesManagementPage.clickEditButton().generateOnuID().clickSaveAndReconfigureButton();
        assertEquals(accessLineRiRobot.getAllocatedOnuIdFromAccessLine(accessLine).get(0), initialOnuid);
    }

    @Test
    @TmsLink("DIGIHUB-85696")
    @Description("Generate ANP_Tag")
    public void generateAnpTagTest() {
        AccessLineDto accessLine = accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
                        AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, ProfileState.ACTIVE)
                .get(0);
        Integer initialAnpTag = accessLine.getAnpTag().getAnpTag();
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.searchAccessLinesByLineID(accessLine.getLineId()).clickSearchButton()
                .clickMagnifyingGlassForLine(0);
        accessLinesManagementPage.clickEditButton().generateAnpTag().clickSaveAndReconfigureButton();
        assertNotEquals(accessLinesManagementPage.getAnpTag(), initialAnpTag.toString());
        accessLinesManagementPage.clickEditButton().generateAnpTag().clickSaveAndReconfigureButton();
        assertEquals(accessLine.getAnpTag().getAnpTag(), initialAnpTag);
    }
}
