package com.tsystems.tm.acc.ta.team.upiter.accesslinesearch;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.address.AddressCase;
import com.tsystems.tm.acc.data.upiter.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.Address;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.pages.osr.accessmanagement.AccessLineSearchPage;
import com.tsystems.tm.acc.ta.pages.osr.accessmanagement.AccessLinesManagementPage;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLineProductionPlatform;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLineTechnology;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessTransmissionMedium;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import groovy.util.logging.Slf4j;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
  private UpiterTestContext context = UpiterTestContext.get();
  private AccessLine accessLinesByEndSz;
  private AccessLine accessLineByHomeId;
  private Address addressWithKlsId;
  private Ont ontSerialNumber;
  private AccessLine accessLine;
  private AccessLine a4AccessLine;
  private AccessLine accessLineFttbTp;

  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot = new AccessLineRiRobot();
    accessLinesByEndSz = context.getData().getAccessLineDataProvider().get(AccessLineCase.linesByEndSz);
    accessLineByHomeId = context.getData().getAccessLineDataProvider().get(AccessLineCase.linesByHomeId);
    addressWithKlsId = context.getData().getAddressDataProvider().get(AddressCase.linesByKlsId);
    ontSerialNumber = context.getData().getOntDataProvider().get(OntCase.linesByOnt);
    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOTelekomNSOOpsRW);
    setCredentials(loginData.getLogin(), loginData.getPassword());
    a4AccessLine = new AccessLine();
    accessLine = new AccessLine();
    accessLineFttbTp = new AccessLine();
    accessLineRiRobot.clearDatabase();
    accessLineRiRobot.fillDatabaseForOltCommissioningV1();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseAddFttbLinesToOltDevice();
  }

  @Test
  @TmsLink("DIGIHUB-39501")
  @Description("Search by EndSz, check for basic information")
  public void searchAccessLinesByEndSzTest() {
    AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
    accessLineSearchPage.validateUrl();
    accessLineSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSz)
            .clickSearchButton();

    accessLineSearchPage.checkTableHeaders(accessLineSearchPage.getTableHeaders());
    accessLineSearchPage.checkTableMessagePattern(accessLineSearchPage.getTableMessage());
    accessLineSearchPage.checkPaginationSizes(accessLineSearchPage.getPaginatorSizes());
    accessLineSearchPage.setWalledGardenStatus().searchAccessLinesByPortAddress(accessLinesByEndSz);
    accessLineSearchPage.checkBasicInformation();
    AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);
    accessLinesManagementPage.checkAccessLineProfilesStates("ACTIVE", "NULL", "ACTIVE", "NULL");
  }

  @Test
  @TmsLink("DIGIHUB-39501")
  @Description("Check sort by endSz, slot number and port number")
  public void sortingBySlotAndPortNumbersTest() {
    AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
    accessLineSearchPage.validateUrl();
    accessLineSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSz).clickSearchButton();
    accessLineSearchPage.setPageSize(100);
    accessLineSearchPage.checkSortOfTable(accessLineSearchPage.getTableLines());
  }

  @Test
  @TmsLink("DIGIHUB-39499")
  @Description("Search by HomeID, check for basic information")
  public void searchAccessLinesByHomeIdTest() {
    AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
    accessLineSearchPage.validateUrl();
    accessLineSearchPage.searchAccessLinesByHomeID(accessLineByHomeId.getHomeId()).clickSearchButton();
    accessLineSearchPage.checkBasicInformation();
    AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);
    accessLinesManagementPage.checkAccessLineProfilesStates("INACTIVE", "ACTIVE",
            "INACTIVE", "ACTIVE");
  }

  @Test
  @TmsLink("DIGIHUB-39491")
  @Description("Search by LineID, check for basic information, inactive line")
  public void searchAccessLinesByLineIdTest() {
    accessLine.setLineId(accessLineRiRobot.getAccessLinesByType(AccessLineProductionPlatform.OLT_BNG, AccessLineTechnology.GPON, AccessLineStatus.INACTIVE).get(0).getLineId());
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
    AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
    accessLineSearchPage.validateUrl();
    accessLineSearchPage.searchAccessLinesByKlsId(addressWithKlsId.getKlsId()).clickSearchButton();
    accessLineSearchPage.checkBasicInformation();
    AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(1);
    accessLinesManagementPage.checkAccessLineProfilesStates("INACTIVE", "ACTIVE",
            "INACTIVE", "ACTIVE");
  }

  @Test
  @TmsLink("DIGIHUB-66819")
  @Description("Search by ONT S/N")
  public void searchAccessLinesByOntSnTest() {
    AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
    accessLineSearchPage.validateUrl();
    accessLineSearchPage.searchAccessLinesByOntSn(ontSerialNumber.getSerialNumber()).clickSearchButton();
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
            .setWalledGardenStatus()
            .setInactiveStatus();
    accessLineSearchPage.checkBasicInformation();
    assertTrue(accessLineSearchPage.sortIconIsPresentInStatusColumn(), "Sort icon is not present in status column");
    accessLineSearchPage.sortAccessLinesByStatus()
            .setPageSize(10);
    assertEquals(accessLineSearchPage.getTableLines().get(0).getStatus(), AccessLineStatus.INACTIVE, "Table wasn't sorted");
    AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);
    accessLinesManagementPage.checkAccessLineProfilesStates("ACTIVE", "NULL",
            "NULL", "NULL");
  }

  @Test
  @TmsLink("DIGIHUB-81428")
  @Description("Add subscriber_ne_profile and Save and Reconfigure")
  public void addSubscriberNeProfileTest() {
    AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
    accessLineSearchPage.validateUrl();
    AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSz)
            .setWalledGardenStatus()
            .clickMagnifyingGlassForLine(0);
    String lineId = accessLinesManagementPage.getLineId();
    accessLinesManagementPage.clickEditButton()
            .addSubscriberNeProfile()
            .changeDefaultProfileStateToInactive()
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
            .searchAccessLinesByLineID(accessLine.getLineId())
            .clickSearchButton()
            .clickMagnifyingGlassForLine(0);
    accessLinesManagementPage.checkAccessLineProfilesStates("ACTIVE", "NULL",
            "ACTIVE", "NULL");
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()), AccessLineStatus.WALLED_GARDEN);
  }

  @Test
  @TmsLink("DIGIHUB-125016")
  @Description("Add serialnumber for A4 AccessLine and save locally")
  public void saveLocallyA4Al() {
    a4AccessLine.setLineId(accessLineRiRobot.getAccessLinesByType(AccessLineProductionPlatform.A4, AccessLineTechnology.GPON, AccessLineStatus.WALLED_GARDEN).get(0).getLineId());
    AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
    accessLineSearchPage.validateUrl();
    AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.searchAccessLinesByLineID(a4AccessLine.getLineId()).clickSearchButton()
            .clickMagnifyingGlassForLine(0);
    accessLinesManagementPage.clickEditButton()
            .changeAccessLineStatusToAssigned()
            .addSerialNumberToNSp(ontSerialNumber.getSerialNumber())
            .changeDefaultNLProfileStateToInactive()
            .addSubscriberNLProfile(addressWithKlsId.getKlsId())
            .clickSaveLocalButton()
            .closeCurrentTab();
    accessLinesManagementPage.returnToAccessLinesSearchPage()
            .searchAccessLinesByLineID(a4AccessLine.getLineId())
            .clickSearchButton()
            .clickMagnifyingGlassForLine(0);
    accessLinesManagementPage.checkNLProfiles("INACTIVE", "ACTIVE");
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber(), ontSerialNumber.getSerialNumber());
  }

  @Test
  @TmsLink("DIGIHUB-125017")
  @Description("Add Subscriber Profile for FTTB AccessLine and save locally")
  public void saveLocallyFttbAL() {
    accessLineFttbTp.setLineId(accessLineRiRobot.getFttbAccessLines(AccessTransmissionMedium.TWISTED_PAIR, AccessLineStatus.WALLED_GARDEN).get(0).getLineId());
    AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
    accessLineSearchPage.validateUrl();
    AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.searchAccessLinesByLineID(accessLineFttbTp.getLineId()).clickSearchButton()
            .clickMagnifyingGlassForLine(0);
    accessLinesManagementPage.clickEditButton()
            .changeAccessLineStatusToAssigned()
            .changeDefaultNLProfileStateToInactive()
            .addSubscriberNLProfile(addressWithKlsId.getKlsId())
            .clickSaveLocalButton()
            .closeCurrentTab();
    accessLinesManagementPage.returnToAccessLinesSearchPage()
            .searchAccessLinesByLineID(accessLineFttbTp.getLineId())
            .clickSearchButton()
            .clickMagnifyingGlassForLine(0);
    accessLinesManagementPage.checkFTTBProfiles("INACTIVE", "ACTIVE", "ACTIVE", "ACTIVE");
  }

  @Test
  @TmsLink("DIGIHUB-96235")
  @Description("ONT Abmeldung, AccessLine is set to Walled_Garden")
  public void ontAbmeldungTest() {
    AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
    accessLineSearchPage.validateUrl();
    accessLineSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSz).setAssignedStatus();
    AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);
    String lineId = accessLinesManagementPage.getLineId();
    accessLinesManagementPage.clickEditButton()
            .clickOntAbmeldungButton()
            .clickBestätigenButton()
            .clickEditButton()
            .changeAccessLineStatusToWalledGarden()
            .clickSaveAndReconfigureButton()
            .closeCurrentTab();
    accessLinesManagementPage.returnToAccessLinesSearchPage()
            .searchAccessLinesByLineID(lineId)
            .clickSearchButton()
            .clickMagnifyingGlassForLine(0);
    accessLinesManagementPage.checkAccessLineProfilesStates("ACTIVE", "NULL",
            "ACTIVE", "NULL");
  }
}
