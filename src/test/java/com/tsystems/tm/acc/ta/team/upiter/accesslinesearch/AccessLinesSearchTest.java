package com.tsystems.tm.acc.ta.team.upiter.accesslinesearch;

import com.tsystems.tm.acc.data.models.credentials.Credentials;
import com.tsystems.tm.acc.data.osr.models.accessline.AccessLine;
import com.tsystems.tm.acc.data.osr.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.accessmanagement.AccessLineSearchPage;
import com.tsystems.tm.acc.ta.pages.osr.accessmanagement.AccessLineSearchPage.ProfileNames;
import com.tsystems.tm.acc.ta.pages.osr.accessmanagement.AccessLineSearchPage.ProfileTypes;
import com.tsystems.tm.acc.ta.ui.UITest;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
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

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.HTTP_CODE_OK_200;

public class AccessLinesSearchTest extends UITest {

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
        RHSSOAuthListener.resetLoginData(loginData.getLogin(), loginData.getPassword());
        RHSSOAuthListener.startListening();
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
        AccessLineSearchPage accessManagementSearchPage = AccessLineSearchPage.openPage();
        accessManagementSearchPage.validateUrl();
        accessManagementSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSz);

        checkTableHeaders(accessManagementSearchPage.getTableHeaders());
        checkTableMessagePattern(accessManagementSearchPage.getTableMessage());
        checkPaginationSizes(accessManagementSearchPage.getPaginatorSizes());
        accessManagementSearchPage.setWalledGardenStatus().searchAccessLinesByPortAddress(accessLinesByEndSz);
        checkBasicInformation(accessManagementSearchPage,
                "state: ACTIVE", "Subscriber Profile",
                "state: ACTIVE", "Subscriber Profile");
    }

    @Test
    @TmsLink("DIGIHUB-39501")
    @Description("Check sort by endSz, slot number and port number")
    public void sortingBySlotAndPortNumbersTest() {
        AccessLineSearchPage accessManagementSearchPage = AccessLineSearchPage.openPage();
        accessManagementSearchPage.validateUrl();
        accessManagementSearchPage.searchAccessLinesByPortAddress(accessLinesByEndSz);

        accessManagementSearchPage.setPageSize(100);
        checkSortOfTable(accessManagementSearchPage.getTableLines());
    }

    @Test
    @TmsLink("DIGIHUB-39499")
    @Description("Search by HomeID, check for basic information")
    public void searchAccessLinesByHomeIdTest() {
        AccessLineSearchPage accessManagementSearchPage = AccessLineSearchPage.openPage();
        accessManagementSearchPage.validateUrl();
        accessManagementSearchPage.searchAccessLinesByHomeID(accessLineByHomeId);

        checkBasicInformation(accessManagementSearchPage,
                "state: INACTIVE", "state: ACTIVE",
                "state: INACTIVE", "state: ACTIVE");
    }

    @Test
    @TmsLink("DIGIHUB-39491")
    @Description("Search by LineID, check for basic information, inactive line")
    public void searchAccessLinesByLineIdTest() {
        AccessLineSearchPage accessManagementSearchPage = AccessLineSearchPage.openPage();
        accessManagementSearchPage.validateUrl();
        accessManagementSearchPage.searchAccessLinesByLineID(accessLineByLineId);

        checkBasicInformation(accessManagementSearchPage,
                "state: ACTIVE", "Subscriber Profile",
                "Default Profile", "Subscriber Profile");
    }

    private void checkBasicInformation(AccessLineSearchPage accessManagementSearchPage,
                                       String neDefaultText, String neSubscriberText,
                                       String networkDefaultText, String networkSubscriberText) {
        checkTableHeaders(accessManagementSearchPage.getTableHeaders());
        checkTableMessagePattern(accessManagementSearchPage.getTableMessage());
        checkPaginationSizes(accessManagementSearchPage.getPaginatorSizes());
        checkRowExpansionInformation(accessManagementSearchPage,
                neDefaultText, neSubscriberText,
                networkDefaultText, networkSubscriberText);
    }

    private void checkTableHeaders(List<String> tableHeaders) {
        List<String> supposedHeaders = Arrays.asList("EndSZ", "HomeID", "LineID", "Port", "Slot", "Status", "Access Platform");
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

    private void checkRowExpansionInformation(AccessLineSearchPage accessManagementSearchPage,
                                              String neDefaultText, String neSubscriberText,
                                              String networkDefaultText, String networkSubscriberText) {
        String neDefaultProfileInfo = accessManagementSearchPage.getProfileText(ProfileTypes.NE_PROFILE, ProfileNames.DEFAULT_PROFILE, 0);
        String neSubscriberProfileInfo = accessManagementSearchPage.getProfileText(ProfileTypes.NE_PROFILE, ProfileNames.SUBSCRIBER_PROFILE, 0);
        String networkDefaultProfileInfo = accessManagementSearchPage.getProfileText(ProfileTypes.NETWORK_LINE_PROFILE, ProfileNames.DEFAULT_PROFILE, 0);
        String networkSubscriberProfileInfo = accessManagementSearchPage.getProfileText(ProfileTypes.NETWORK_LINE_PROFILE, ProfileNames.SUBSCRIBER_PROFILE, 0);

        Assert.assertTrue(neDefaultProfileInfo.contains(neDefaultText));
        Assert.assertTrue(neSubscriberProfileInfo.contains(neSubscriberText));
        Assert.assertTrue(networkDefaultProfileInfo.contains(networkDefaultText));
        Assert.assertTrue(networkSubscriberProfileInfo.contains(networkSubscriberText));
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
