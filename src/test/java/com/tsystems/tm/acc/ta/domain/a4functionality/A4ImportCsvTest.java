package com.tsystems.tm.acc.ta.domain.a4functionality;

import com.tsystems.tm.acc.data.osr.models.a4importcsvdata.A4ImportCsvDataCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.osr.RetryLoop;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ImportPage;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryImporterUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

@Epic("OS&R")
@Feature("Import Network Element (Group) CSV file into A4 Resource Inventory")
@TmsLink("DIGIHUB-124555")
@ServiceLog({
        A4_RESOURCE_INVENTORY_UI_MS,
        A4_RESOURCE_INVENTORY_BFF_PROXY_MS,
        A4_RESOURCE_INVENTORY_MS,
        A4_RESOURCE_INVENTORY_SERVICE_MS,
        A4_NEMO_UPDATER_MS})
public class A4ImportCsvTest extends GigabitTest {

    private final OsrTestContext context = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryImporterUiRobot a4ResourceInventoryImporterUiRobot = new A4ResourceInventoryImporterUiRobot();
    private final A4NemoUpdaterRobot a4NemoUpdaterRobot = new A4NemoUpdaterRobot();
    private WireMockMappingsContext mappingsContext = new OsrWireMockMappingsContextBuilder(
            new WireMockMappingsContext(WireMockFactory.get(), "")).build();
    private A4ImportCsvData csvData;

    @BeforeClass
    public void init() {
        csvData = context.getData().getA4ImportCsvDataDataProvider().get(A4ImportCsvDataCase.defaultCsvFile);
        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4ImportCsvTest"))
                .addNemoMock()
                .build();
        new RetryLoop().withCondition(() -> A4ImportPage.login().validate() != null).assertMessage("Import page not available");
        A4ImportPage.login().validate();
        mappingsContext.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());
    }

    @AfterMethod
    public void cleanup() {
        mappingsContext.close();
        mappingsContext
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());
        // Delete all A4 data which might provoke problems because of unique constraints
        a4ResourceInventoryRobot.deleteA4TestDataRecursively(csvData);
    }

    @Test(description = "DIGIHUB-124555 Import Network Element (Group) CSV file into A4 Resource Inventory")
    @Owner("Phillip.Moeller@t-systems.com, Anita.Junge@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-124555")
    @Description("Import Network Element (Group) CSV file into A4 Resource Inventory")
    public void testImportCsvFile() {
        // When / Action
        a4ResourceInventoryImporterUiRobot.importCsvFileViaUi(csvData);

        // Then / Assert
        a4ResourceInventoryRobot.checkNetworkElementByCsvData(csvData);
        a4ResourceInventoryRobot.checkNetworkElementPortsByImportCsvData(csvData);
        a4NemoUpdaterRobot.checkAsyncNemoUpdatePutRequests(csvData);
    }
}
