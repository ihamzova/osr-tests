package com.tsystems.tm.acc.ta.team.upiter.accesslinesearch;

import com.tsystems.tm.acc.data.models.stable.Credentials;
import com.tsystems.tm.acc.data.osr.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.accessmanagement.AccessLineSearchPage;
import com.tsystems.tm.acc.ta.pages.osr.accessmanagement.AccessLinesManagementPage;
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

import static com.tsystems.tm.acc.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.HTTP_CODE_OK_200;

public class AccessLinesSearchTest extends BaseTest {

    private AccessLineResourceInventoryClient alResourceInventory;
    private AccessLine accessLinesByEndSz;
    private AccessLine accessLineByHomeId;
    private AccessLine accessLineByLineId;

    @BeforeClass
    public void init() throws InterruptedException {
        alResourceInventory = new AccessLineResourceInventoryClient();
        OsrTestContext context = OsrTestContext.get();
        accessLinesByEndSz = context.getData().getAccessLineDataProvider().get(AccessLineCase.linesByEndSz);
        accessLineByHomeId = context.getData().getAccessLineDataProvider().get(AccessLineCase.linesByHomeId);
        accessLineByLineId = context.getData().getAccessLineDataProvider().get(AccessLineCase.linesByLineId);
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOAccessManagementSupportUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
        prepareData();
    }

    @AfterClass
    public void clearData() {
        clearDataBase();
    }

    @Test
    @TmsLink("DIGIHUB-39501")
    @Description("Search by EndSz, check for basic information")
    public void searchAccessLinesByEndSzTest() {
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSz);

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
        accessLineSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSz);

        accessLineSearchPage.setPageSize(100);
        checkSortOfTable(accessLineSearchPage.getTableLines());
    }

    @Test
    @TmsLink("DIGIHUB-39499")
    @Description("Search by HomeID, check for basic information")
    public void searchAccessLinesByHomeIdTest() {
        AccessLineSearchPage accessLineSearchPage = AccessLineSearchPage.openPage();
        accessLineSearchPage.validateUrl();
        accessLineSearchPage.searchAccessLinesByHomeID(accessLineByHomeId);

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
        accessLineSearchPage.searchAccessLinesByLineID(accessLineByLineId);

        checkBasicInformation(accessLineSearchPage);

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
        clearDataBase();
        Thread.sleep(3000);
        fillDataBase();
    }

    private void clearDataBase() {
        alResourceInventory.getClient().fillDatabase().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private void fillDataBase() {
        alResourceInventory.getClient().fillDatabase().fillDatabaseForOltCommissioning()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

}
