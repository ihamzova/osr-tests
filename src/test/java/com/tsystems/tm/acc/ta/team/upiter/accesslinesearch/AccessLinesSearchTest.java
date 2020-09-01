package com.tsystems.tm.acc.ta.team.upiter.accesslinesearch;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.address.AddressCase;
import com.tsystems.tm.acc.data.upiter.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.Address;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.pages.osr.accessmanagement.AccessLineSearchPage;
import com.tsystems.tm.acc.ta.pages.osr.accessmanagement.AccessLinesManagementPage;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.AccessLineViewDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;

@ServiceLog(ACCESS_MANAGEMENT_SUPPORT_UI_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(ACCESS_LINE_BFF_PROXY_MS)
public class AccessLinesSearchTest extends BaseTest {

    private AccessLineRiRobot accessLineRiRobot;
    private AccessLineResourceInventoryClient alResourceInventory;
    private UpiterTestContext context = UpiterTestContext.get();
    private AccessLine accessLinesByEndSz;
    private AccessLine accessLineByHomeId;
    private AccessLine accessLineByLineId;
    private Address addressWithKlsId;
    private Ont ontSerialNumber;

    @BeforeClass
    public void init() throws InterruptedException {
        accessLineRiRobot = new AccessLineRiRobot();
        alResourceInventory = new AccessLineResourceInventoryClient();
        accessLinesByEndSz = context.getData().getAccessLineDataProvider().get(AccessLineCase.linesByEndSz);
        accessLineByHomeId = context.getData().getAccessLineDataProvider().get(AccessLineCase.linesByHomeId);
        accessLineByLineId = context.getData().getAccessLineDataProvider().get(AccessLineCase.linesByLineId);
        addressWithKlsId = context.getData().getAddressDataProvider().get(AddressCase.linesByKlsId);
        ontSerialNumber = context.getData().getOntDataProvider().get(OntCase.linesByOnt);
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOTelekomNSOOpsRW);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
        prepareData();
    }

    @AfterClass
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @Test
    @TmsLink("DIGIHUB-39501")
    @Description("Search by EndSz, check for basic information")
    public void searchAccessLinesByEndSzTest() {
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSz)
                .clickSearchButton();

        checkTableHeaders(accessLineSearchPage.getTableHeaders());
        checkTableMessagePattern(accessLineSearchPage.getTableMessage());
        checkPaginationSizes(accessLineSearchPage.getPaginatorSizes());
        accessLineSearchPage.setWalledGardenStatus().searchAccessLinesByPortAddress(accessLinesByEndSz);

        checkBasicInformation(accessLineSearchPage);

        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);
        checkAccessLinesManagementStates(accessLinesManagementPage, "ACTIVE", "NULL",
                "ACTIVE", "NULL");
    }

    @Test
    @TmsLink("DIGIHUB-39501")
    @Description("Check sort by endSz, slot number and port number")
    public void sortingBySlotAndPortNumbersTest() {
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSz).clickSearchButton();

        accessLineSearchPage.setPageSize(100);
        checkSortOfTable(accessLineSearchPage.getTableLines());
    }

    @Test
    @TmsLink("DIGIHUB-39499")
    @Description("Search by HomeID, check for basic information")
    public void searchAccessLinesByHomeIdTest() {
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByHomeID(accessLineByHomeId).clickSearchButton();

        checkBasicInformation(accessLineSearchPage);

        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);

        checkAccessLinesManagementStates(accessLinesManagementPage, "INACTIVE", "ACTIVE",
                "INACTIVE","ACTIVE");
    }

    @Test
    @TmsLink("DIGIHUB-39491")
    @Description("Search by LineID, check for basic information, inactive line")
    public void searchAccessLinesByLineIdTest() {
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByLineID(accessLineByLineId).clickSearchButton();

        checkBasicInformation(accessLineSearchPage);

        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);

        checkAccessLinesManagementStates(accessLinesManagementPage, "ACTIVE", "NULL",
                "NULL", "NULL");

    }

    @Test
    @TmsLink("DIGIHUB-66818")
    @Description("Search by KLS ID")
    public void searchAccessLinesByKlsIdTest() {
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByKlsId(addressWithKlsId.getKlsId()).clickSearchButton();

        checkBasicInformation(accessLineSearchPage);

        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(1);

        checkAccessLinesManagementStates(accessLinesManagementPage, "INACTIVE", "ACTIVE",
                "INACTIVE", "ACTIVE");
    }

    @Test
    @TmsLink("DIGIHUB-66819")
    @Description("Search by ONT S/N")
    public void searchAccessLinesByOntSnTest() {
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByOntSn(ontSerialNumber.getSerialNumber()).clickSearchButton();

        checkBasicInformation(accessLineSearchPage);

        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);

        checkAccessLinesManagementStates(accessLinesManagementPage, "INACTIVE", "ACTIVE",
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

        checkBasicInformation(accessLineSearchPage);

        Assert.assertTrue(accessLineSearchPage.sortIconIsPresentInStatusColumn(), "Sort icon is not present in status column");

        accessLineSearchPage.sortAccessLinesByStatus();

        Assert.assertEquals(accessLineSearchPage.getTableLines().get(0).getStatus(),AccessLineViewDto.StatusEnum.INACTIVE, "Table wasn't sorted");

        AccessLinesManagementPage accessLinesManagementPage = accessLineSearchPage.clickMagnifyingGlassForLine(0);

        checkAccessLinesManagementStates(accessLinesManagementPage, "ACTIVE", "NULL",
                "NULL", "NULL");
    }

    private void checkBasicInformation(AccessLineSearchPage accessLineSearchPage) {
        checkTableHeaders(accessLineSearchPage.getTableHeaders());
        checkTableMessagePattern(accessLineSearchPage.getTableMessage());
        checkPaginationSizes(accessLineSearchPage.getPaginatorSizes());
    }

    private void checkTableHeaders(List<String> tableHeaders) {
        List<String> supposedHeaders = Arrays.asList("EndSZ", "Slot", "Port", "Line ID", "Home ID", "Access Platform", "Status");
        Assert.assertEqualsNoOrder(tableHeaders.stream().filter(header -> !header.isEmpty()).toArray(),
                supposedHeaders.toArray());
    }

    private void checkTableMessagePattern(String tableMessage) {
        String supposedPattern = "\\d+ Access Lines? wurden? gefunden";
        Assert.assertTrue(Pattern.matches(supposedPattern, tableMessage));
    }

    private void checkPaginationSizes(List<String> paginatorSizes) {
        List<String> supposedSizes = Arrays.asList("10", "20", "50", "100");
        Assert.assertEqualsNoOrder(paginatorSizes.toArray(), supposedSizes.toArray());
    }

    private void checkSortOfTable(List<AccessLineViewDto> tableRows) {
        List<AccessLineViewDto> supposedOrder = new ArrayList<>(tableRows);
        supposedOrder.sort((row1, row2) -> {
            if (row1.getSlotNumber().equals(row2.getSlotNumber())) {
                return row1.getPortNumber().compareTo(row2.getPortNumber());
            } else {
                return row1.getSlotNumber().compareTo(
                        row2.getSlotNumber());
            }
        });
        Assert.assertEquals(tableRows, supposedOrder);
    }

    private void checkAccessLinesManagementStates(AccessLinesManagementPage page, String neExpectedDefaultProfileState,
                                                  String neExpectedSubscriberProfileState, String nlExpectedDefaultProfileState,
                                                  String nlExpectedSubscriberProfileState) {
        Assert.assertTrue(page.getNEDefaultProfileState().contains(neExpectedDefaultProfileState));
        Assert.assertTrue(page.getNESubscriberProfileState().contains(neExpectedSubscriberProfileState));
        Assert.assertTrue(page.getNLDefaultProfileState().contains(nlExpectedDefaultProfileState));
        Assert.assertTrue(page.getNLSubscriberProfileState().contains(nlExpectedSubscriberProfileState));
    }

    private void prepareData() throws InterruptedException {
        accessLineRiRobot.clearDatabase();
        accessLineRiRobot.fillDatabaseForOltCommissioning();
    }
}
